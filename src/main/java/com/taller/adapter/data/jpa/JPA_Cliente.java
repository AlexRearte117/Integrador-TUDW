package com.taller.adapter.data.jpa;

import com.taller.adapter.data.entity.DTO_Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JPA_Cliente extends JpaRepository<DTO_Cliente, UUID> {
}
