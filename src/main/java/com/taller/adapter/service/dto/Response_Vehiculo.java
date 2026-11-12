package com.taller.adapter.service.dto;

import java.util.UUID;

public record Response_Vehiculo(UUID id, String patente, String marca, String modelo, UUID clienteId) {
}
