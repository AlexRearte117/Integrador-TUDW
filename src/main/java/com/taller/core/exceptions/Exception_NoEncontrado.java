package com.taller.core.exceptions;

/**
 * Excepción usada cuando un recurso solicitado no existe (HTTP 404).
 */
public class Exception_NoEncontrado extends RuntimeException {
    public Exception_NoEncontrado(String message) {
        super(message);
    }
}
