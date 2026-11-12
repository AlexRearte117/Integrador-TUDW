package com.taller.adapter.data.mapper;

import com.taller.adapter.data.entity.DTO_HistorialEstado;
import com.taller.adapter.data.entity.DTO_Orden;
import com.taller.adapter.data.entity.DTO_OrdenRepuesto;
import com.taller.adapter.service.dto.Response_CambioEstado;
import com.taller.adapter.service.dto.Response_ItemRepuesto;
import com.taller.adapter.service.dto.Response_Orden;
import com.taller.core.models.EstadoOrden;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public final class Mapper_Orden {

    private Mapper_Orden() {
    }

    public static Response_Orden toResponse(DTO_Orden orden, String patente) {
        BigDecimal costoRepuestos = calcularCostoRepuestos(orden);

        List<Response_CambioEstado> historial = orden.getHistorial().stream()
                .map(cambio -> new Response_CambioEstado(
                        cambio.getEstado().name(),
                        cambio.getFechaHora()))
                .toList();

        List<Response_ItemRepuesto> repuestos = orden.getRepuestos().stream()
                .map(item -> {
                    BigDecimal precio = precioUnitario(item);
                    BigDecimal subtotal = precio
                            .multiply(BigDecimal.valueOf(item.getCantidad()))
                            .setScale(2, RoundingMode.HALF_UP);

                    return new Response_ItemRepuesto(
                            item.getRepuesto() != null ? item.getRepuesto().getId() : null,
                            item.getRepuesto() != null ? item.getRepuesto().getNombre() : null,
                            item.getCantidad(),
                            precio,
                            subtotal);
                })
                .toList();

        return new Response_Orden(
                orden.getId(),
                orden.getVehiculoId(),
                patente,
                orden.getDescripcion(),
                orden.getEstado().name(),
                orden.getCostoFinal(),
                costoRepuestos,
                orden.getFechaCreacion(),
                historial,
                repuestos,
                calcularHorasEnReparacion(orden.getHistorial()));
    }

    public static BigDecimal calcularCostoRepuestos(DTO_Orden orden) {
        if (orden == null || orden.getRepuestos() == null || orden.getRepuestos().isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal total = BigDecimal.ZERO;
        for (DTO_OrdenRepuesto item : orden.getRepuestos()) {
            total = total.add(precioUnitario(item)
                    .multiply(BigDecimal.valueOf(item.getCantidad())));
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public static Double calcularHorasEnReparacion(List<DTO_HistorialEstado> historial) {
        if (historial == null || historial.isEmpty()) {
            return null;
        }

        LocalDateTime enReparacion = null;
        LocalDateTime terminado = null;

        for (DTO_HistorialEstado cambio : historial) {
            if (cambio.getEstado() == EstadoOrden.EN_REPARACION && enReparacion == null) {
                enReparacion = cambio.getFechaHora();
            }
            if (cambio.getEstado() == EstadoOrden.TERMINADO && terminado == null) {
                terminado = cambio.getFechaHora();
            }
        }

        if (enReparacion == null || terminado == null || terminado.isBefore(enReparacion)) {
            return null;
        }

        double horas = Duration.between(enReparacion, terminado).toSeconds() / 3600.0;
        return Math.round(horas * 100.0) / 100.0;
    }

    private static BigDecimal precioUnitario(DTO_OrdenRepuesto item) {
        if (item.getPrecioUnitario() != null) {
            return item.getPrecioUnitario();
        }
        if (item.getRepuesto() != null && item.getRepuesto().getPrecio() != null) {
            return item.getRepuesto().getPrecio();
        }
        return BigDecimal.ZERO;
    }
}
