package com.proyecto.microservicioclientes.exceptions;

// Clases de excepciones personalizadas
public class ClienteNotFoundException extends RuntimeException {
    public ClienteNotFoundException(String message) {
        super(message);
    }
}
