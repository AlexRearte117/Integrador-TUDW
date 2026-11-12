package com.taller.core.models;

import com.taller.core.exceptions.Exception_Orden;
import com.taller.core.exceptions.Exception_Conflicto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Orden {
    private UUID id_orden;
    private UUID vehiculoId;
    private String descripcion;
    private BigDecimal costoFinal;
    private EstadoOrden estado;
    private LocalDateTime fechaCreacion;
    private List<CambioEstado> historial = new ArrayList<>();
    private List<ItemRepuesto> repuestos = new ArrayList<>();

    private Orden() {
    }

    public static Orden crear(UUID vehiculoId, String descripcion) {
        if (vehiculoId == null) {
            throw new Exception_Orden("El vehículo es obligatorio");
        }

        if (descripcion == null || descripcion.isBlank()) {
            throw new Exception_Orden("La descripción es obligatoria");
        }

        Orden orden = new Orden();
        orden.vehiculoId = vehiculoId;
        orden.descripcion = descripcion.trim();
        orden.estado = EstadoOrden.INGRESADO;
        orden.fechaCreacion = LocalDateTime.now();
        orden.historial.add(new CambioEstado(EstadoOrden.INGRESADO, orden.fechaCreacion));
        return orden;
    }

    public void cambiarEstado(EstadoOrden nuevoEstado, LocalDateTime fechaHora) {
        if (nuevoEstado == null) {
            throw new Exception_Orden("El estado es obligatorio");
        }

        if (nuevoEstado == estado) {
            return;
        }

        if (!estado.puedeTransicionarA(nuevoEstado)) {
            throw new Exception_Conflicto(
                    "No se puede pasar de " + estado + " a " + nuevoEstado);
        }

        if (nuevoEstado == EstadoOrden.ENTREGADO && costoFinal == null) {
            throw new Exception_Conflicto(
                    "No se puede entregar una orden sin costo final cargado");
        }

        this.estado = nuevoEstado;
        this.historial.add(new CambioEstado(nuevoEstado, fechaHora));
    }

    public static BigDecimal calcularCostoRepuestos(List<ItemRepuesto> repuestos) {
        if (repuestos == null || repuestos.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return repuestos.stream()
                .map(ItemRepuesto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static Double calcularHorasEnReparacion(List<CambioEstado> historial) {
        if (historial == null || historial.isEmpty()) {
            return null;
        }

        LocalDateTime enReparacion = null;
        LocalDateTime terminado = null;

        for (CambioEstado cambio : historial) {
            if (cambio.getEstado() == EstadoOrden.EN_REPARACION && enReparacion == null) {
                enReparacion = cambio.getFechaHora();
            }
            if (cambio.getEstado() == EstadoOrden.TERMINADO && terminado == null) {
                terminado = cambio.getFechaHora();
            }
        }

        if (enReparacion == null || terminado == null || terminado.isBefore(enReparacion)) {
            return null;
        }

        double horas = Duration.between(enReparacion, terminado).toSeconds() / 3600.0;
        return Math.round(horas * 100.0) / 100.0;
    }

    public UUID getId_orden() {
        return id_orden;
    }

    public void setId_orden(UUID id_orden) {
        this.id_orden = id_orden;
    }

    public UUID getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(UUID vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getCostoFinal() {
        return costoFinal;
    }

    public void setCostoFinal(BigDecimal costoFinal) {
        this.costoFinal = costoFinal;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<CambioEstado> getHistorial() {
        return historial;
    }

    public void setHistorial(List<CambioEstado> historial) {
        this.historial = historial;
    }

    public List<ItemRepuesto> getRepuestos() {
        return repuestos;
    }

    public void setRepuestos(List<ItemRepuesto> repuestos) {
        this.repuestos = repuestos;
    }
}
