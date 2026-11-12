package com.taller.core.models;

import com.taller.core.exceptions.Exception_Cliente;

import java.util.UUID;

public class Cliente {
    private UUID id_Cliente;
    private String nombre;
    private String telefono;

    public Cliente(UUID id_Cliente, String nombre, String telefono) {
        this.id_Cliente = id_Cliente;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public static Cliente Instancia_Cliente(UUID id_Cliente, String nombre, String telefono) throws Exception_Cliente {
        if (nombre == null || nombre.isBlank()) {
            throw new Exception_Cliente("El nombre del cliente es obligatorio");
        }

        if (telefono == null || telefono.isBlank()) {
            throw new Exception_Cliente("El telefono del cliente es obligatorio");
        }

        return new Cliente(id_Cliente, nombre.trim(), telefono.trim());
    }

    public UUID getId_Cliente() {
        return id_Cliente;
    }

    public void setId_Cliente(UUID id_Cliente) {
        this.id_Cliente = id_Cliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
