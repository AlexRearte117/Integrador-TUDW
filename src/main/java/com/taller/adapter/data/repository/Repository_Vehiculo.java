package com.taller.adapter.data.repository;

import com.taller.adapter.data.entity.DTO_Vehiculo;
import com.taller.adapter.data.jpa.JPA_Vehiculo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class Repository_Vehiculo {

    private final JPA_Vehiculo jpaVehiculo;

    public Repository_Vehiculo(JPA_Vehiculo jpaVehiculo) {
        this.jpaVehiculo = jpaVehiculo;
    }

    public List<DTO_Vehiculo> findAll() {
        return jpaVehiculo.findAll();
    }

    public Optional<DTO_Vehiculo> findById(UUID id) {
        return jpaVehiculo.findById(id);
    }

    public Optional<DTO_Vehiculo> findByPatente(String patente) {
        return jpaVehiculo.findByPatente(patente);
    }

    public List<DTO_Vehiculo> findByClienteId(UUID clienteId) {
        return jpaVehiculo.findByClienteId(clienteId);
    }

    public DTO_Vehiculo save(DTO_Vehiculo vehiculo) {
        return jpaVehiculo.save(vehiculo);
    }

    public void deleteById(UUID id) {
        jpaVehiculo.deleteById(id);
    }

    public boolean existsByPatente(String patente) {
        return jpaVehiculo.existsByPatente(patente);
    }

    public boolean existsByPatenteAndIdNot(String patente, UUID id) {
        return jpaVehiculo.existsByPatenteAndIdNot(patente, id);
    }

    public boolean existsByClienteId(UUID clienteId) {
        return jpaVehiculo.existsByClienteId(clienteId);
    }
}
