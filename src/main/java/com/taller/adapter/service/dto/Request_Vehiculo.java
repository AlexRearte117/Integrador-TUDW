package com.taller.adapter.service.dto;

import java.util.UUID;

public record Request_Vehiculo(String patente, String marca, String modelo, UUID clienteId) {
}
