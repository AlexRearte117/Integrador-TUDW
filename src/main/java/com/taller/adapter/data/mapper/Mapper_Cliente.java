package com.taller.adapter.data.mapper;

import com.taller.adapter.data.entity.DTO_Cliente;
import com.taller.adapter.service.dto.Response_Cliente;
import com.taller.core.models.Cliente;

public final class Mapper_Cliente {

    private Mapper_Cliente() {
    }

    public static DTO_Cliente toEntity(Cliente cliente) {
        return new DTO_Cliente(
                cliente.getId_Cliente(),
                cliente.getNombre(),
                cliente.getTelefono());
    }

    public static Cliente toDomain(DTO_Cliente entity) {
        return new Cliente(
                entity.getId_Cliente(),
                entity.getNombre(),
                entity.getTelefono());
    }

    public static Response_Cliente toResponse(DTO_Cliente entity) {
        return new Response_Cliente(
                entity.getId_Cliente(),
                entity.getNombre(),
                entity.getTelefono());
    }
}
