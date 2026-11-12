package com.taller.adapter.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "vehiculo",
        uniqueConstraints = @UniqueConstraint(name = "uk_vehiculo_patente", columnNames = "patente"))
public class DTO_Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_vehiculo", unique = true, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 10)
    private String patente;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String modelo;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;
}
