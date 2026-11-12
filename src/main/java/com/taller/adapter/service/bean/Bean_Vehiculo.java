package com.taller.adapter.service.bean;

import com.taller.adapter.data.entity.DTO_Orden;
import com.taller.adapter.data.entity.DTO_Vehiculo;
import com.taller.adapter.data.mapper.Mapper_Orden;
import com.taller.adapter.data.mapper.Mapper_Vehiculo;
import com.taller.adapter.data.repository.Repository_Cliente;
import com.taller.adapter.data.repository.Repository_Orden;
import com.taller.adapter.data.repository.Repository_Vehiculo;
import com.taller.adapter.service.dto.Request_Vehiculo;
import com.taller.adapter.service.dto.Response_Orden;
import com.taller.adapter.service.dto.Response_Vehiculo;
import com.taller.core.exceptions.Exception_Conflicto;
import com.taller.core.exceptions.Exception_NoEncontrado;
import com.taller.core.models.Vehiculo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class Bean_Vehiculo {

    private final Repository_Vehiculo repositoryVehiculo;
    private final Repository_Cliente repositoryCliente;
    private final Repository_Orden repositoryOrden;

    public Bean_Vehiculo(Repository_Vehiculo repositoryVehiculo,
                         Repository_Cliente repositoryCliente,
                         Repository_Orden repositoryOrden) {
        this.repositoryVehiculo = repositoryVehiculo;
        this.repositoryCliente = repositoryCliente;
        this.repositoryOrden = repositoryOrden;
    }

    @Transactional(readOnly = true)
    public List<Response_Vehiculo> listar() {
        return repositoryVehiculo.findAll().stream()
                .map(Mapper_Vehiculo::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Response_Vehiculo obtener(UUID id) {
        return Mapper_Vehiculo.toResponse(buscar(id));
    }

    public Response_Vehiculo crear(Request_Vehiculo request) {
        Vehiculo vehiculo = Vehiculo.Intancia_vehiculo(
                null,
                request != null ? request.patente() : null,
                request != null ? request.marca() : null,
                request != null ? request.modelo() : null,
                request != null ? request.clienteId() : null);

        verificarCliente(vehiculo.getClienteId());

        if (repositoryVehiculo.existsByPatente(vehiculo.getPatente())) {
            throw new Exception_Conflicto(
                    "Ya existe un vehículo con la patente " + vehiculo.getPatente());
        }

        DTO_Vehiculo guardado = repositoryVehiculo.save(Mapper_Vehiculo.toEntity(vehiculo));
        return Mapper_Vehiculo.toResponse(guardado);
    }

    public Response_Vehiculo actualizar(UUID id, Request_Vehiculo request) {
        DTO_Vehiculo entity = buscar(id);

        Vehiculo vehiculo = Vehiculo.Intancia_vehiculo(
                entity.getId(),
                request != null ? request.patente() : null,
                request != null ? request.marca() : null,
                request != null ? request.modelo() : null,
                request != null ? request.clienteId() : null);

        verificarCliente(vehiculo.getClienteId());

        if (repositoryVehiculo.existsByPatenteAndIdNot(vehiculo.getPatente(), id)) {
            throw new Exception_Conflicto(
                    "Ya existe otro vehículo con la patente " + vehiculo.getPatente());
        }

        entity.setPatente(vehiculo.getPatente());
        entity.setMarca(vehiculo.getMarca());
        entity.setModelo(vehiculo.getModelo());
        entity.setClienteId(vehiculo.getClienteId());

        return Mapper_Vehiculo.toResponse(repositoryVehiculo.save(entity));
    }

    public void eliminar(UUID id) {
        buscar(id);

        if (repositoryOrden.existsByVehiculoId(id)) {
            throw new Exception_Conflicto(
                    "No se puede eliminar el vehículo porque tiene órdenes asociadas");
        }

        repositoryVehiculo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Response_Orden> historialPorPatente(String patente) {
        if (patente == null || patente.isBlank()) {
            throw new Exception_NoEncontrado("Vehículo no encontrado");
        }

        String patenteNormalizada = patente.trim().toUpperCase(Locale.ROOT);
        DTO_Vehiculo vehiculo = repositoryVehiculo.findByPatente(patenteNormalizada)
                .orElseThrow(() -> new Exception_NoEncontrado(
                        "Vehículo no encontrado con patente " + patenteNormalizada));

        List<DTO_Orden> ordenes =
                repositoryOrden.findByVehiculoIdOrderByFechaCreacionDesc(vehiculo.getId());

        return ordenes.stream()
                .map(orden -> Mapper_Orden.toResponse(orden, vehiculo.getPatente()))
                .toList();
    }

    private DTO_Vehiculo buscar(UUID id) {
        if (id == null) {
            throw new Exception_NoEncontrado("Vehículo no encontrado");
        }

        return repositoryVehiculo.findById(id)
                .orElseThrow(() -> new Exception_NoEncontrado("Vehículo no encontrado: " + id));
    }

    private void verificarCliente(UUID clienteId) {
        if (clienteId == null || !repositoryCliente.existsById(clienteId)) {
            throw new Exception_NoEncontrado("Cliente no encontrado: " + clienteId);
        }
    }
}
