package org.example.hackaton01.exception;

/**
 * Excepción lanzada cuando un usuario intenta acceder a un recurso sin los permisos necesarios.
 * HTTP Status: 403 FORBIDDEN
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
