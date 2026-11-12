package com.taller.adapter.service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record Response_Repuesto(UUID id, String nombre, BigDecimal precio, Integer stock) {
}
