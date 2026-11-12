package com.taller.core.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class ItemRepuesto {
    private final UUID repuestoId;
    private final String nombre;
    private final BigDecimal precioUnitario;
    private final int cantidad;

    public ItemRepuesto(UUID repuestoId, String nombre, BigDecimal precioUnitario, int cantidad) {
        this.repuestoId = repuestoId;
        this.nombre = nombre;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
    }

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
    }

    public UUID getRepuestoId() {
        return repuestoId;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public int getCantidad() {
        return cantidad;
    }
}
