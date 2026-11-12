package com.taller.adapter.service.controller;

import com.taller.adapter.service.bean.Bean_Cliente;
import com.taller.adapter.service.dto.Request_Cliente;
import com.taller.adapter.service.dto.Response_Cliente;
import com.taller.adapter.service.dto.Response_Vehiculo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
public class Controller_Cliente {

    private final Bean_Cliente beanCliente;

    public Controller_Cliente(Bean_Cliente beanCliente) {
        this.beanCliente = beanCliente;
    }

    @GetMapping
    public List<Response_Cliente> listar() {
        return beanCliente.listar();
    }

    @GetMapping("/{id}")
    public Response_Cliente obtener(@PathVariable UUID id) {
        return beanCliente.obtener(id);
    }

    @PostMapping
    public ResponseEntity<Response_Cliente> crear(@RequestBody(required = false) Request_Cliente request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(beanCliente.crear(request));
    }

    @PutMapping("/{id}")
    public Response_Cliente actualizar(@PathVariable UUID id,
                                       @RequestBody(required = false) Request_Cliente request) {
        return beanCliente.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        beanCliente.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/vehiculos")
    public List<Response_Vehiculo> vehiculos(@PathVariable UUID id) {
        return beanCliente.vehiculos(id);
    }
}
