package com.taller.adapter.service.bean;

import com.taller.adapter.data.entity.DTO_Cliente;
import com.taller.adapter.data.entity.DTO_Vehiculo;
import com.taller.adapter.data.mapper.Mapper_Cliente;
import com.taller.adapter.data.mapper.Mapper_Vehiculo;
import com.taller.adapter.data.repository.Repository_Cliente;
import com.taller.adapter.data.repository.Repository_Vehiculo;
import com.taller.adapter.service.dto.Request_Cliente;
import com.taller.adapter.service.dto.Response_Cliente;
import com.taller.adapter.service.dto.Response_Vehiculo;
import com.taller.core.exceptions.Exception_Conflicto;
import com.taller.core.exceptions.Exception_NoEncontrado;
import com.taller.core.models.Cliente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class Bean_Cliente {

    private final Repository_Cliente repositoryCliente;
    private final Repository_Vehiculo repositoryVehiculo;

    public Bean_Cliente(Repository_Cliente repositoryCliente, Repository_Vehiculo repositoryVehiculo) {
        this.repositoryCliente = repositoryCliente;
        this.repositoryVehiculo = repositoryVehiculo;
    }

    @Transactional(readOnly = true)
    public List<Response_Cliente> listar() {
        return repositoryCliente.findAll().stream()
                .map(Mapper_Cliente::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Response_Cliente obtener(UUID id) {
        return Mapper_Cliente.toResponse(buscar(id));
    }

    public Response_Cliente crear(Request_Cliente request) {
        Cliente cliente = Cliente.Instancia_Cliente(
                null,
                request != null ? request.nombre() : null,
                request != null ? request.telefono() : null);

        DTO_Cliente guardado = repositoryCliente.save(Mapper_Cliente.toEntity(cliente));
        return Mapper_Cliente.toResponse(guardado);
    }

    public Response_Cliente actualizar(UUID id, Request_Cliente request) {
        DTO_Cliente entity = buscar(id);

        Cliente cliente = Cliente.Instancia_Cliente(
                entity.getId_Cliente(),
                request != null ? request.nombre() : null,
                request != null ? request.telefono() : null);

        entity.setNombre(cliente.getNombre());
        entity.setTelefono(cliente.getTelefono());

        return Mapper_Cliente.toResponse(repositoryCliente.save(entity));
    }

    public void eliminar(UUID id) {
        buscar(id);

        if (repositoryVehiculo.existsByClienteId(id)) {
            throw new Exception_Conflicto(
                    "No se puede eliminar el cliente porque tiene vehículos asociados");
        }

        repositoryCliente.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Response_Vehiculo> vehiculos(UUID clienteId) {
        buscar(clienteId);

        return repositoryVehiculo.findByClienteId(clienteId).stream()
                .map(Mapper_Vehiculo::toResponse)
                .toList();
    }

    private DTO_Cliente buscar(UUID id) {
        if (id == null) {
            throw new Exception_NoEncontrado("Cliente no encontrado");
        }

        return repositoryCliente.findById(id)
                .orElseThrow(() -> new Exception_NoEncontrado("Cliente no encontrado: " + id));
    }
}
