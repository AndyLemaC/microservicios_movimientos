package com.proyecto.microservicioclientes.exceptions;

public class DuplicateIdentificacionException extends RuntimeException {
    public DuplicateIdentificacionException(String message) {
        super(message);
    }
}
