package com.taller.adapter.service.bean;

import com.taller.adapter.data.entity.DTO_Repuesto;
import com.taller.adapter.data.mapper.Mapper_Repuesto;
import com.taller.adapter.data.repository.Repository_Repuesto;
import com.taller.adapter.service.dto.Request_Repuesto;
import com.taller.adapter.service.dto.Response_Repuesto;
import com.taller.core.exceptions.Exception_Conflicto;
import com.taller.core.exceptions.Exception_NoEncontrado;
import com.taller.core.models.Repuesto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class Bean_Repuesto {

    private final Repository_Repuesto repositoryRepuesto;

    public Bean_Repuesto(Repository_Repuesto repositoryRepuesto) {
        this.repositoryRepuesto = repositoryRepuesto;
    }

    @Transactional(readOnly = true)
    public List<Response_Repuesto> listar() {
        return repositoryRepuesto.findAll().stream()
                .map(Mapper_Repuesto::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Response_Repuesto obtener(UUID id) {
        return Mapper_Repuesto.toResponse(buscar(id));
    }

    public Response_Repuesto crear(Request_Repuesto request) {
        Repuesto repuesto = Repuesto.Instancia_Repuesto(
                null,
                request != null ? request.nombre() : null,
                request != null ? request.precio() : null,
                request != null ? request.stock() : null);

        DTO_Repuesto guardado = repositoryRepuesto.save(Mapper_Repuesto.toEntity(repuesto));
        return Mapper_Repuesto.toResponse(guardado);
    }

    public Response_Repuesto actualizar(UUID id, Request_Repuesto request) {
        DTO_Repuesto entity = buscar(id);

        Repuesto repuesto = Repuesto.Instancia_Repuesto(
                entity.getId(),
                request != null ? request.nombre() : null,
                request != null ? request.precio() : null,
                request != null ? request.stock() : null);

        entity.setNombre(repuesto.getNombre());
        entity.setPrecio(repuesto.getPrecio());
        entity.setStock(repuesto.getStock());

        return Mapper_Repuesto.toResponse(repositoryRepuesto.save(entity));
    }

    public void eliminar(UUID id) {
        buscar(id);

        if (repositoryRepuesto.estaUsadoEnOrdenes(id)) {
            throw new Exception_Conflicto(
                    "No se puede eliminar el repuesto porque está asociado a una orden");
        }

        repositoryRepuesto.deleteById(id);
    }

    private DTO_Repuesto buscar(UUID id) {
        if (id == null) {
            throw new Exception_NoEncontrado("Repuesto no encontrado");
        }

        return repositoryRepuesto.findById(id)
                .orElseThrow(() -> new Exception_NoEncontrado("Repuesto no encontrado: " + id));
    }
}
