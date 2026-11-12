package com.taller.adapter.data.mapper;

import com.taller.adapter.data.entity.DTO_Vehiculo;
import com.taller.adapter.service.dto.Response_Vehiculo;
import com.taller.core.models.Vehiculo;

public final class Mapper_Vehiculo {

    private Mapper_Vehiculo() {
    }

    public static DTO_Vehiculo toEntity(Vehiculo vehiculo) {
        return new DTO_Vehiculo(
                vehiculo.getId_vehiculo(),
                vehiculo.getPatente(),
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getClienteId());
    }

    public static Vehiculo toDomain(DTO_Vehiculo entity) {
        return new Vehiculo(
                entity.getId(),
                entity.getPatente(),
                entity.getMarca(),
                entity.getModelo(),
                entity.getClienteId());
    }

    public static Response_Vehiculo toResponse(DTO_Vehiculo entity) {
        return new Response_Vehiculo(
                entity.getId(),
                entity.getPatente(),
                entity.getMarca(),
                entity.getModelo(),
                entity.getClienteId());
    }
}
