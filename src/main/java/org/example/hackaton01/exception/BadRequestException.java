package org.example.hackaton01.exception;

/**
 * Excepción lanzada cuando la petición del cliente es inválida.
 * HTTP Status: 400 BAD REQUEST
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
