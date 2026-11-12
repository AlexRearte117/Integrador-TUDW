package com.taller.adapter.data.jpa;

import com.taller.adapter.data.entity.DTO_Repuesto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JPA_Repuesto extends JpaRepository<DTO_Repuesto, UUID> {
}
