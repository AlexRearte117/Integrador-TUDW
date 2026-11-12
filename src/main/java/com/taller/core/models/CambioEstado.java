package com.taller.core.models;

import java.time.LocalDateTime;

public class CambioEstado {
    private final EstadoOrden estado;
    private final LocalDateTime fechaHora;

    public CambioEstado(EstadoOrden estado, LocalDateTime fechaHora) {
        this.estado = estado;
        this.fechaHora = fechaHora;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}
