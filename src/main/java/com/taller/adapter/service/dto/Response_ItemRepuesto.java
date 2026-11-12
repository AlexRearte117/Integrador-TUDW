package com.taller.adapter.service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record Response_ItemRepuesto(
        UUID repuestoId,
        String nombre,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal) {
}
