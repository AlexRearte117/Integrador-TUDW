package com.taller.core.exceptions;

/**
 * Excepción usada para reglas de negocio que representan un conflicto (HTTP 409).
 */
public class Exception_Conflicto extends RuntimeException {
    public Exception_Conflicto(String message) {
        super(message);
    }
}
