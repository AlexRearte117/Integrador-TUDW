package com.taller.adapter.service.controller;

import com.taller.adapter.service.bean.Bean_Orden;
import com.taller.adapter.service.dto.Request_AgregarRepuesto;
import com.taller.adapter.service.dto.Request_Estado;
import com.taller.adapter.service.dto.Request_Orden;
import com.taller.adapter.service.dto.Request_OrdenUpdate;
import com.taller.adapter.service.dto.Response_Facturacion;
import com.taller.adapter.service.dto.Response_Orden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ordenes")
public class Controller_Orden {

    private final Bean_Orden beanOrden;

    public Controller_Orden(Bean_Orden beanOrden) {
        this.beanOrden = beanOrden;
    }

    @GetMapping
    public List<Response_Orden> listar(@RequestParam(required = false) String estado) {
        return beanOrden.listar(estado);
    }

    @GetMapping("/facturacion")
    public Response_Facturacion facturacion(@RequestParam(required = false) String mes) {
        return beanOrden.facturacion(mes);
    }

    @GetMapping("/{id}")
    public Response_Orden obtener(@PathVariable UUID id) {
        return beanOrden.obtener(id);
    }

    @PostMapping
    public ResponseEntity<Response_Orden> crear(@RequestBody(required = false) Request_Orden request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(beanOrden.crear(request));
    }

    @PutMapping("/{id}")
    public Response_Orden actualizar(@PathVariable UUID id,
                                     @RequestBody(required = false) Request_OrdenUpdate request) {
        return beanOrden.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public Response_Orden cambiarEstado(@PathVariable UUID id,
                                        @RequestBody(required = false) Request_Estado request) {
        return beanOrden.cambiarEstado(id, request != null ? request.estado() : null);
    }

    @PutMapping("/{id}/estado")
    public Response_Orden cambiarEstadoPut(@PathVariable UUID id,
                                           @RequestBody(required = false) Request_Estado request) {
        return beanOrden.cambiarEstado(id, request != null ? request.estado() : null);
    }

    @PostMapping("/{id}/repuestos")
    public Response_Orden agregarRepuesto(@PathVariable UUID id,
                                          @RequestBody(required = false) Request_AgregarRepuesto request) {
        return beanOrden.agregarRepuesto(id, request);
    }
}
