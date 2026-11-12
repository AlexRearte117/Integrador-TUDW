package com.taller.adapter.data.entity;

import com.taller.core.models.EstadoOrden;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orden")
public class DTO_Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_orden", unique = true, nullable = false)
    private UUID id;

    @Column(name = "vehiculo_id", nullable = false)
    private UUID vehiculoId;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Column(name = "costo_final", precision = 14, scale = 2)
    private BigDecimal costoFinal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoOrden estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("fechaHora ASC")
    private List<DTO_HistorialEstado> historial = new ArrayList<>();

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private List<DTO_OrdenRepuesto> repuestos = new ArrayList<>();

    public void agregarHistorial(DTO_HistorialEstado cambio) {
        cambio.setOrden(this);
        this.historial.add(cambio);
    }

    public void agregarRepuesto(DTO_OrdenRepuesto item) {
        item.setOrden(this);
        this.repuestos.add(item);
    }
}
