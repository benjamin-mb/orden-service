package com.arka.orden_service.exceptions;

public class OrdenInvalidStateException extends RuntimeException {
  public OrdenInvalidStateException(String message) {
    super(message);
  }
}
