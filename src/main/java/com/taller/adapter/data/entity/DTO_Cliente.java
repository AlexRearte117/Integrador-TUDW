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
@Table(name = "cliente")
public class DTO_Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_cliente", columnDefinition = "BINARY(16)", unique = true, nullable = false)
    private UUID id_Cliente;
    private String nombre;
    private String telefono;

}
