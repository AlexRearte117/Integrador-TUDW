package com.taller.adapter.service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Response_Orden(
        UUID id,
        UUID vehiculoId,
        String patente,
        String descripcion,
        String estado,
        BigDecimal costoFinal,
        BigDecimal costoRepuestos,
        LocalDateTime fechaCreacion,
        List<Response_CambioEstado> historial,
        List<Response_ItemRepuesto> repuestos,
        Double horasEnReparacion) {
}
