package org.example.hackaton01.exception;

/**
 * Excepción lanzada cuando un servicio externo no está disponible (LLM, Email, etc).
 * HTTP Status: 503 SERVICE UNAVAILABLE
 */
public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
