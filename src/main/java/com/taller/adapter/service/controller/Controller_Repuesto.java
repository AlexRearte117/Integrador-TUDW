package com.taller.adapter.service.controller;

import com.taller.adapter.service.bean.Bean_Repuesto;
import com.taller.adapter.service.dto.Request_Repuesto;
import com.taller.adapter.service.dto.Response_Repuesto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/repuestos")
public class Controller_Repuesto {

    private final Bean_Repuesto beanRepuesto;

    public Controller_Repuesto(Bean_Repuesto beanRepuesto) {
        this.beanRepuesto = beanRepuesto;
    }

    @GetMapping
    public List<Response_Repuesto> listar() {
        return beanRepuesto.listar();
    }

    @GetMapping("/{id}")
    public Response_Repuesto obtener(@PathVariable UUID id) {
        return beanRepuesto.obtener(id);
    }

    @PostMapping
    public ResponseEntity<Response_Repuesto> crear(@RequestBody(required = false) Request_Repuesto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(beanRepuesto.crear(request));
    }

    @PutMapping("/{id}")
    public Response_Repuesto actualizar(@PathVariable UUID id,
                                        @RequestBody(required = false) Request_Repuesto request) {
        return beanRepuesto.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        beanRepuesto.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
