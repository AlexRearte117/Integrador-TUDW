package com.taller.adapter.data.jpa;

import com.taller.adapter.data.entity.DTO_OrdenRepuesto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JPA_OrdenRepuesto extends JpaRepository<DTO_OrdenRepuesto, UUID> {

    boolean existsByRepuestoId(UUID id_repuesto);
}
