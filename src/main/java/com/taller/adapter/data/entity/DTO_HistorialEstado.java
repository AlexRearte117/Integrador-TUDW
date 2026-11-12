package com.taller.adapter.data.entity;

import com.taller.core.models.EstadoOrden;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "historial_estado")
public class DTO_HistorialEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_historial", unique = true, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orden_id", nullable = false)
    private DTO_Orden orden;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoOrden estado;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    public DTO_HistorialEstado(DTO_Orden orden, EstadoOrden estado, LocalDateTime fechaHora) {
        this.orden = orden;
        this.estado = estado;
        this.fechaHora = fechaHora;
    }
}
