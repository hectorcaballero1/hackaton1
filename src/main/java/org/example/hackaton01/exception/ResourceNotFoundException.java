package org.example.hackaton01.exception;

/**
 * Excepción lanzada cuando un recurso solicitado no existe en la base de datos.
 * HTTP Status: 404 NOT FOUND
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
