package com.taller.adapter.data.jpa;

import com.taller.adapter.data.entity.DTO_Orden;
import com.taller.core.models.EstadoOrden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface JPA_Orden extends JpaRepository<DTO_Orden, UUID> {

    List<DTO_Orden> findByEstado(EstadoOrden estado);

    List<DTO_Orden> findByVehiculoIdOrderByFechaCreacionDesc(UUID vehiculoId);

    boolean existsByVehiculoId(UUID vehiculoId);

    boolean existsByVehiculoIdAndEstadoNotIn(UUID vehiculoId, Collection<EstadoOrden> estados);
}
