package com.arka.orden_service.exceptions;

public class OrdenNotFoundException extends RuntimeException {
    public OrdenNotFoundException(String message) {
        super(message);
    }
}
