package com.taller.adapter.service.dto;

import java.math.BigDecimal;

/**
 * Los campos nulos no se modifican. El estado se recibe como texto para poder
 * devolver un 400 con un mensaje claro si no es un estado válido.
 */
public record Request_OrdenUpdate(String descripcion, BigDecimal costoFinal, String estado) {
}
