package org.example.hackaton01.exception;

/**
 * Excepción lanzada cuando la autenticación falla.
 * HTTP Status: 401 UNAUTHORIZED
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
