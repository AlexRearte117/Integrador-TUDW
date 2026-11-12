package com.taller.core.models;

import com.taller.core.exceptions.Exception_Repuesto;

import java.math.BigDecimal;
import java.util.UUID;

public class Repuesto {
    private UUID id_repuesto;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;

    public Repuesto(UUID id_repuesto, String nombre, BigDecimal precio, Integer stock) {
        this.id_repuesto = id_repuesto;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    public static Repuesto Instancia_Repuesto(UUID id_repuesto, String nombre, BigDecimal precio, Integer stock)
            throws Exception_Repuesto {
        if (nombre == null || nombre.isBlank()) {
            throw new Exception_Repuesto("El nombre del repuesto es obligatorio");
        }

        if (precio == null || precio.signum() < 0) {
            throw new Exception_Repuesto("El precio del repuesto debe ser mayor o igual a cero");
        }

        if (stock == null || stock < 0) {
            throw new Exception_Repuesto("El stock del repuesto debe ser mayor o igual a cero");
        }

        return new Repuesto(id_repuesto, nombre.trim(), precio, stock);
    }

    public UUID getId_repuesto() {
        return id_repuesto;
    }

    public void setId_repuesto(UUID id_repuesto) {
        this.id_repuesto = id_repuesto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
