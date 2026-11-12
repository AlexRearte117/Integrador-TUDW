package com.taller.adapter.service.controller;

import com.taller.adapter.data.api.Api_Error;
import com.taller.core.exceptions.Exception_Cliente;
import com.taller.core.exceptions.Exception_Conflicto;
import com.taller.core.exceptions.Exception_NoEncontrado;
import com.taller.core.exceptions.Exception_Orden;
import com.taller.core.exceptions.Exception_Repuesto;
import com.taller.core.exceptions.Exception_vehiculo;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class Controller_ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(Controller_ExceptionHandler.class);

    @ExceptionHandler(Exception_NoEncontrado.class)
    public ResponseEntity<Api_Error> noEncontrado(Exception_NoEncontrado ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(Exception_Conflicto.class)
    public ResponseEntity<Api_Error> conflicto(Exception_Conflicto ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Api_Error> rutaNoEncontrada(NoResourceFoundException ex,
                                                      HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Recurso no encontrado", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Api_Error> metodoNoSoportado(HttpRequestMethodNotSupportedException ex,
                                                       HttpServletRequest request) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP no soportado", request);
    }

    @ExceptionHandler({
            Exception_Cliente.class,
            Exception_vehiculo.class,
            Exception_Orden.class,
            Exception_Repuesto.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<Api_Error> solicitudInvalida(RuntimeException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<Api_Error> formatoInvalido(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "La solicitud no tiene un formato válido", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Api_Error> integridad(DataIntegrityViolationException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "La operación viola una restricción de la base de datos", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Api_Error> errorInterno(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado en {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", request);
    }

    private ResponseEntity<Api_Error> build(HttpStatus status, String message, HttpServletRequest request) {
        Api_Error error = new Api_Error(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }
}
