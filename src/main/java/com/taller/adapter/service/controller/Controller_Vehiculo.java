package com.taller.adapter.service.controller;

import com.taller.adapter.service.bean.Bean_Vehiculo;
import com.taller.adapter.service.dto.Request_Vehiculo;
import com.taller.adapter.service.dto.Response_Orden;
import com.taller.adapter.service.dto.Response_Vehiculo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehiculos")
public class Controller_Vehiculo {

    private final Bean_Vehiculo beanVehiculo;

    public Controller_Vehiculo(Bean_Vehiculo beanVehiculo) {
        this.beanVehiculo = beanVehiculo;
    }

    @GetMapping
    public List<Response_Vehiculo> listar() {
        return beanVehiculo.listar();
    }

    @GetMapping("/{id}")
    public Response_Vehiculo obtener(@PathVariable UUID id) {
        return beanVehiculo.obtener(id);
    }

    @PostMapping
    public ResponseEntity<Response_Vehiculo> crear(@RequestBody(required = false) Request_Vehiculo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(beanVehiculo.crear(request));
    }

    @PutMapping("/{id}")
    public Response_Vehiculo actualizar(@PathVariable UUID id,
                                        @RequestBody(required = false) Request_Vehiculo request) {
        return beanVehiculo.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        beanVehiculo.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patente/{patente}/historial")
    public List<Response_Orden> historial(@PathVariable String patente) {
        return beanVehiculo.historialPorPatente(patente);
    }
}
