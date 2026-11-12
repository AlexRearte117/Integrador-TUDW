package com.taller.core.models;

import com.taller.core.exceptions.Exception_vehiculo;

import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

public class Vehiculo {
    /**
     * Patentes argentinas admitidas por el enunciado: ABC123 o AB123CD.
     */
    private static final Pattern FORMATO_PATENTE =
            Pattern.compile("^[A-Z]{3}\\d{3}$|^[A-Z]{2}\\d{3}[A-Z]{2}$");

    private UUID id_vehiculo;
    private String patente;
    private String marca;
    private String modelo;
    private UUID clienteId;

    public Vehiculo(UUID id_vehiculo, String patente, String marca, String modelo) {
        this(id_vehiculo, patente, marca, modelo, null);
    }

    public Vehiculo(UUID id_vehiculo, String patente, String marca, String modelo, UUID clienteId) {
        this.id_vehiculo = id_vehiculo;
        this.patente = patente;
        this.marca = marca;
        this.modelo = modelo;
        this.clienteId = clienteId;
    }

    public static Vehiculo Intancia_vehiculo(UUID id_vehiculo, String patente, String marca, String modelo)
            throws Exception_vehiculo {
        return Intancia_vehiculo(id_vehiculo, patente, marca, modelo, null, false);
    }

    public static Vehiculo Intancia_vehiculo(UUID id_vehiculo, String patente, String marca, String modelo,
                                             UUID clienteId) throws Exception_vehiculo {
        return Intancia_vehiculo(id_vehiculo, patente, marca, modelo, clienteId, true);
    }

    private static Vehiculo Intancia_vehiculo(UUID id_vehiculo, String patente, String marca, String modelo,
                                              UUID clienteId, boolean clienteObligatorio)
            throws Exception_vehiculo {
        if (patente == null || patente.isBlank()) {
            throw new Exception_vehiculo("La patente es obligatoria");
        }

        String patenteNormalizada = patente.trim().toUpperCase(Locale.ROOT);
        if (!FORMATO_PATENTE.matcher(patenteNormalizada).matches()) {
            throw new Exception_vehiculo("La patente debe tener el formato ABC123 o AB123CD");
        }

        if (marca == null || marca.isBlank()) {
            throw new Exception_vehiculo("La marca es obligatoria");
        }

        if (modelo == null || modelo.isBlank()) {
            throw new Exception_vehiculo("El modelo es obligatorio");
        }

        if (clienteObligatorio && clienteId == null) {
            throw new Exception_vehiculo("El cliente es obligatorio");
        }

        return new Vehiculo(id_vehiculo, patenteNormalizada, marca.trim(), modelo.trim(), clienteId);
    }

    public UUID getId_vehiculo() {
        return id_vehiculo;
    }

    public void setId_vehiculo(UUID id_vehiculo) {
        this.id_vehiculo = id_vehiculo;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public void setClienteId(UUID clienteId) {
        this.clienteId = clienteId;
    }
}
