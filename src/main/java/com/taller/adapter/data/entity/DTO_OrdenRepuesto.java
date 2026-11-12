package com.taller.adapter.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orden_repuesto",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_orden_repuesto", columnNames = {"orden_id", "repuesto_id"}))
public class DTO_OrdenRepuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_orden_repuesto", unique = true, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orden_id", nullable = false)
    private DTO_Orden orden;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repuesto_id", nullable = false)
    private DTO_Repuesto repuesto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 14, scale = 2)
    private BigDecimal precioUnitario;
}
