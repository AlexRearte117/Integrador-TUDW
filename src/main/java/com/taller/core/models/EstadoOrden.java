package com.taller.core.models;

import com.taller.core.exceptions.Exception_Orden;

import java.util.Locale;

public enum EstadoOrden {
    INGRESADO,
    EN_REPARACION,
    TERMINADO,
    ENTREGADO,
    CANCELADO;

    public boolean estaAbierta() {
        return this != ENTREGADO && this != CANCELADO;
    }

    public boolean estaTerminada() {
        return this == TERMINADO || this == ENTREGADO;
    }

    public boolean puedeTransicionarA(EstadoOrden destino) {
        if (destino == null) {
            return false;
        }

        return switch (this) {
            case INGRESADO -> destino == EN_REPARACION || destino == CANCELADO;
            case EN_REPARACION -> destino == TERMINADO;
            case TERMINADO -> destino == ENTREGADO;
            case ENTREGADO, CANCELADO -> false;
        };
    }

    public static EstadoOrden desdeTexto(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new Exception_Orden("El estado es obligatorio");
        }

        try {
            return EstadoOrden.valueOf(valor.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new Exception_Orden("Estado inválido: " + valor);
        }
    }
}
