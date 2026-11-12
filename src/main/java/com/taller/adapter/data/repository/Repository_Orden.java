package com.taller.adapter.data.repository;

import com.taller.adapter.data.entity.DTO_Orden;
import com.taller.adapter.data.jpa.JPA_Orden;
import com.taller.core.models.EstadoOrden;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class Repository_Orden {

    private static final List<EstadoOrden> ESTADOS_CERRADOS =
            List.of(EstadoOrden.ENTREGADO, EstadoOrden.CANCELADO);

    private final JPA_Orden jpaOrden;

    public Repository_Orden(JPA_Orden jpaOrden) {
        this.jpaOrden = jpaOrden;
    }

    public List<DTO_Orden> findAll() {
        return jpaOrden.findAll();
    }

    public Optional<DTO_Orden> findById(UUID id) {
        return jpaOrden.findById(id);
    }

    public List<DTO_Orden> findByEstado(EstadoOrden estado) {
        return jpaOrden.findByEstado(estado);
    }

    public List<DTO_Orden> findByVehiculoIdOrderByFechaCreacionDesc(UUID vehiculoId) {
        return jpaOrden.findByVehiculoIdOrderByFechaCreacionDesc(vehiculoId);
    }

    public DTO_Orden save(DTO_Orden orden) {
        return jpaOrden.save(orden);
    }

    public boolean existsByVehiculoId(UUID vehiculoId) {
        return jpaOrden.existsByVehiculoId(vehiculoId);
    }

    public boolean existsOpenByVehiculoId(UUID vehiculoId) {
        return jpaOrden.existsByVehiculoIdAndEstadoNotIn(vehiculoId, ESTADOS_CERRADOS);
    }
}
