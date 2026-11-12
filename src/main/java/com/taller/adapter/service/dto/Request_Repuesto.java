package com.taller.adapter.service.dto;

import java.math.BigDecimal;

public record Request_Repuesto(String nombre, BigDecimal precio, Integer stock) {
}
