package com.taller.adapter.service.bean;

import com.taller.adapter.data.entity.DTO_HistorialEstado;
import com.taller.adapter.data.entity.DTO_Orden;
import com.taller.adapter.data.entity.DTO_OrdenRepuesto;
import com.taller.adapter.data.entity.DTO_Repuesto;
import com.taller.adapter.data.entity.DTO_Vehiculo;
import com.taller.adapter.data.mapper.Mapper_Orden;
import com.taller.adapter.data.repository.Repository_Orden;
import com.taller.adapter.data.repository.Repository_Repuesto;
import com.taller.adapter.data.repository.Repository_Vehiculo;
import com.taller.adapter.service.dto.Request_AgregarRepuesto;
import com.taller.adapter.service.dto.Request_Orden;
import com.taller.adapter.service.dto.Request_OrdenUpdate;
import com.taller.adapter.service.dto.Response_Facturacion;
import com.taller.adapter.service.dto.Response_Orden;
import com.taller.core.exceptions.Exception_Conflicto;
import com.taller.core.exceptions.Exception_NoEncontrado;
import com.taller.core.exceptions.Exception_Orden;
import com.taller.core.exceptions.Exception_Repuesto;
import com.taller.core.models.CambioEstado;
import com.taller.core.models.EstadoOrden;
import com.taller.core.models.Orden;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class Bean_Orden {

    private final Repository_Orden repositoryOrden;
    private final Repository_Vehiculo repositoryVehiculo;
    private final Repository_Repuesto repositoryRepuesto;

    public Bean_Orden(Repository_Orden repositoryOrden,
                      Repository_Vehiculo repositoryVehiculo,
                      Repository_Repuesto repositoryRepuesto) {
        this.repositoryOrden = repositoryOrden;
        this.repositoryVehiculo = repositoryVehiculo;
        this.repositoryRepuesto = repositoryRepuesto;
    }

    @Transactional(readOnly = true)
    public List<Response_Orden> listar(String estado) {
        List<DTO_Orden> ordenes;

        if (estado == null || estado.isBlank()) {
            ordenes = repositoryOrden.findAll();
        } else {
            ordenes = repositoryOrden.findByEstado(EstadoOrden.desdeTexto(estado));
        }

        return ordenes.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Response_Orden obtener(UUID id) {
        return toResponse(buscar(id));
    }

    public Response_Orden crear(Request_Orden request) {
        Orden orden = Orden.crear(
                request != null ? request.vehiculoId() : null,
                request != null ? request.descripcion() : null);

        DTO_Vehiculo vehiculo = repositoryVehiculo.findById(orden.getVehiculoId())
                .orElseThrow(() -> new Exception_NoEncontrado(
                        "Vehículo no encontrado: " + orden.getVehiculoId()));

        if (repositoryOrden.existsOpenByVehiculoId(orden.getVehiculoId())) {
            throw new Exception_Conflicto(
                    "El vehículo ya tiene una orden abierta (ni entregada ni cancelada)");
        }

        DTO_Orden entity = new DTO_Orden();
        entity.setVehiculoId(orden.getVehiculoId());
        entity.setDescripcion(orden.getDescripcion());
        entity.setEstado(orden.getEstado());
        entity.setFechaCreacion(orden.getFechaCreacion());

        for (CambioEstado cambio : orden.getHistorial()) {
            entity.agregarHistorial(new DTO_HistorialEstado(
                    entity, cambio.getEstado(), cambio.getFechaHora()));
        }

        DTO_Orden guardada = repositoryOrden.save(entity);
        return Mapper_Orden.toResponse(guardada, vehiculo.getPatente());
    }

    public Response_Orden actualizar(UUID id, Request_OrdenUpdate request) {
        DTO_Orden orden = buscar(id);

        if (request == null) {
            return toResponse(orden);
        }

        if (request.descripcion() != null) {
            if (request.descripcion().isBlank()) {
                throw new Exception_Orden("La descripción no puede estar vacía");
            }
            orden.setDescripcion(request.descripcion().trim());
        }

        if (request.costoFinal() != null) {
            if (request.costoFinal().signum() < 0) {
                throw new Exception_Orden("El costo final no puede ser negativo");
            }

            BigDecimal costoRepuestos = Mapper_Orden.calcularCostoRepuestos(orden);
            if (costoRepuestos.signum() > 0 && request.costoFinal().compareTo(costoRepuestos) < 0) {
                throw new Exception_Orden(
                        "El costo final no puede ser menor al costo de repuestos ("
                                + costoRepuestos + ")");
            }

            orden.setCostoFinal(request.costoFinal());
        }

        if (request.estado() != null && !request.estado().isBlank()) {
            EstadoOrden nuevoEstado = EstadoOrden.desdeTexto(request.estado());

            if (nuevoEstado != orden.getEstado()) {
                if (!orden.getEstado().puedeTransicionarA(nuevoEstado)) {
                    throw new Exception_Conflicto(
                            "No se puede pasar de " + orden.getEstado() + " a " + nuevoEstado);
                }

                if (nuevoEstado == EstadoOrden.ENTREGADO && orden.getCostoFinal() == null) {
                    throw new Exception_Conflicto(
                            "No se puede entregar una orden sin costo final cargado");
                }

                orden.setEstado(nuevoEstado);
                orden.agregarHistorial(new DTO_HistorialEstado(
                        orden, nuevoEstado, LocalDateTime.now()));
            }
        }

        repositoryOrden.save(orden);
        return toResponse(orden);
    }

    public Response_Orden cambiarEstado(UUID id, String estado) {
        if (estado == null || estado.isBlank()) {
            throw new Exception_Orden("El estado es obligatorio");
        }

        return actualizar(id, new Request_OrdenUpdate(null, null, estado));
    }

    public Response_Orden agregarRepuesto(UUID id, Request_AgregarRepuesto request) {
        DTO_Orden orden = buscar(id);

        if (!orden.getEstado().estaAbierta()) {
            throw new Exception_Conflicto(
                    "No se pueden agregar repuestos a una orden en estado " + orden.getEstado());
        }

        if (request == null || request.repuestoId() == null) {
            throw new Exception_Repuesto("El repuesto es obligatorio");
        }

        if (request.cantidad() == null || request.cantidad() <= 0) {
            throw new Exception_Repuesto("La cantidad debe ser mayor a cero");
        }

        DTO_Repuesto repuesto = repositoryRepuesto.findById(request.repuestoId())
                .orElseThrow(() -> new Exception_NoEncontrado(
                        "Repuesto no encontrado: " + request.repuestoId()));

        int stock = repuesto.getStock() != null ? repuesto.getStock() : 0;
        if (stock < request.cantidad()) {
            throw new Exception_Conflicto(
                    "Stock insuficiente para el repuesto " + repuesto.getNombre()
                            + ". Disponible: " + stock + ", solicitado: " + request.cantidad());
        }

        DTO_OrdenRepuesto existente = orden.getRepuestos().stream()
                .filter(item -> item.getRepuesto() != null
                        && item.getRepuesto().getId().equals(repuesto.getId()))
                .findFirst()
                .orElse(null);

        if (existente != null) {
            existente.setCantidad(existente.getCantidad() + request.cantidad());
        } else {
            DTO_OrdenRepuesto item = new DTO_OrdenRepuesto();
            item.setRepuesto(repuesto);
            item.setCantidad(request.cantidad());
            item.setPrecioUnitario(repuesto.getPrecio());
            orden.agregarRepuesto(item);
        }

        repuesto.setStock(stock - request.cantidad());
        repositoryRepuesto.save(repuesto);
        repositoryOrden.save(orden);

        return toResponse(orden);
    }

    @Transactional(readOnly = true)
    public Response_Facturacion facturacion(String mes) {
        if (mes == null || mes.isBlank()) {
            throw new Exception_Orden("El mes es obligatorio (formato yyyy-MM)");
        }

        YearMonth yearMonth;
        try {
            yearMonth = YearMonth.parse(mes.trim());
        } catch (DateTimeParseException ex) {
            throw new Exception_Orden("El mes debe tener el formato yyyy-MM");
        }

        LocalDateTime inicio = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime fin = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        List<DTO_Orden> entregadas = repositoryOrden.findByEstado(EstadoOrden.ENTREGADO);

        long cantidad = 0;
        BigDecimal total = BigDecimal.ZERO;

        for (DTO_Orden orden : entregadas) {
            LocalDateTime fechaEntrega = orden.getHistorial().stream()
                    .filter(cambio -> cambio.getEstado() == EstadoOrden.ENTREGADO)
                    .map(DTO_HistorialEstado::getFechaHora)
                    .findFirst()
                    .orElse(null);

            if (fechaEntrega == null
                    || fechaEntrega.isBefore(inicio)
                    || !fechaEntrega.isBefore(fin)) {
                continue;
            }

            cantidad++;
            if (orden.getCostoFinal() != null) {
                total = total.add(orden.getCostoFinal());
            }
        }

        return new Response_Facturacion(
                yearMonth.toString(),
                cantidad,
                total.setScale(2, RoundingMode.HALF_UP));
    }

    private DTO_Orden buscar(UUID id) {
        if (id == null) {
            throw new Exception_NoEncontrado("Orden no encontrada");
        }

        return repositoryOrden.findById(id)
                .orElseThrow(() -> new Exception_NoEncontrado("Orden no encontrada: " + id));
    }

    private Response_Orden toResponse(DTO_Orden orden) {
        String patente = repositoryVehiculo.findById(orden.getVehiculoId())
                .map(DTO_Vehiculo::getPatente)
                .orElse(null);

        return Mapper_Orden.toResponse(orden, patente);
    }
}
