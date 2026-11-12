package com.taller.adapter.data.mapper;

import com.taller.adapter.data.entity.DTO_Repuesto;
import com.taller.adapter.service.dto.Response_Repuesto;
import com.taller.core.models.Repuesto;

public final class Mapper_Repuesto {

    private Mapper_Repuesto() {
    }

    public static DTO_Repuesto toEntity(Repuesto repuesto) {
        return new DTO_Repuesto(
                repuesto.getId_repuesto(),
                repuesto.getNombre(),
                repuesto.getPrecio(),
                repuesto.getStock());
    }

    public static Repuesto toDomain(DTO_Repuesto entity) {
        return new Repuesto(
                entity.getId(),
                entity.getNombre(),
                entity.getPrecio(),
                entity.getStock());
    }

    public static Response_Repuesto toResponse(DTO_Repuesto entity) {
        return new Response_Repuesto(
                entity.getId(),
                entity.getNombre(),
                entity.getPrecio(),
                entity.getStock());
    }
}
