package org.example.hackaton01.exception;

/**
 * Excepción lanzada cuando hay un conflicto (ej: username o email duplicado).
 * HTTP Status: 409 CONFLICT
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
