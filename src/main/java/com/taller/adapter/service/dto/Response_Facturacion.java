package com.taller.adapter.service.dto;

import java.math.BigDecimal;

public record Response_Facturacion(
        String mes,
        long cantidadOrdenes,
        long cantidad,
        BigDecimal totalFacturado,
        BigDecimal total) {

    public Response_Facturacion(String mes, long cantidadOrdenes, BigDecimal totalFacturado) {
        this(mes, cantidadOrdenes, cantidadOrdenes, totalFacturado, totalFacturado);
    }
}
