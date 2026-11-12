package com.taller.adapter.data.jpa;

import com.taller.adapter.data.entity.DTO_Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JPA_Vehiculo extends JpaRepository<DTO_Vehiculo, UUID> {

    Optional<DTO_Vehiculo> findByPatente(String patente);

    List<DTO_Vehiculo> findByClienteId(UUID clienteId);

    boolean existsByPatente(String patente);

    boolean existsByPatenteAndIdNot(String patente, UUID id);

    boolean existsByClienteId(UUID clienteId);
}
