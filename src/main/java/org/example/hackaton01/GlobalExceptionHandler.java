package org.example.hackaton01;

import org.example.hackaton01.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones según el formato del README.md
 * Formato estándar para todos los errores:
 * {
 *   "error": "ERROR_TYPE",
 *   "message": "Detalle claro del problema",
 *   "timestamp": "2025-09-12T18:10:00Z",
 *   "path": "/sales"
 * }
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 400 BAD REQUEST - Errores de validación (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("error", "BAD_REQUEST");
        response.put("message", "Validación fallida");
        response.put("details", fieldErrors);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 400 BAD REQUEST - Petición inválida
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(
            BadRequestException ex,
            WebRequest request) {

        Map<String, Object> error = buildErrorResponse("BAD_REQUEST", ex.getMessage(), request);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * 401 UNAUTHORIZED - No autenticado o token inválido
     */
    @ExceptionHandler({UnauthorizedException.class, AuthenticationException.class})
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(
            Exception ex,
            WebRequest request) {

        Map<String, Object> error = buildErrorResponse("UNAUTHORIZED", ex.getMessage(), request);
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 403 FORBIDDEN - Sin permisos para el recurso
     */
    @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
    public ResponseEntity<Map<String, Object>> handleForbiddenException(
            Exception ex,
            WebRequest request) {

        String message = ex.getMessage() != null ? ex.getMessage() : "No tienes permisos para acceder a este recurso";
        Map<String, Object> error = buildErrorResponse("FORBIDDEN", message, request);
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    /**
     * 404 NOT FOUND - Recurso no encontrado
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {

        Map<String, Object> error = buildErrorResponse("NOT_FOUND", ex.getMessage(), request);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * 409 CONFLICT - Conflicto (username/email duplicado)
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflictException(
            ConflictException ex,
            WebRequest request) {

        Map<String, Object> error = buildErrorResponse("CONFLICT", ex.getMessage(), request);
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * 503 SERVICE UNAVAILABLE - Servicio externo caído (LLM, Email)
     */
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleServiceUnavailableException(
            ServiceUnavailableException ex,
            WebRequest request) {

        Map<String, Object> error = buildErrorResponse("SERVICE_UNAVAILABLE", ex.getMessage(), request);
        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * 500 INTERNAL SERVER ERROR - Cualquier otra excepción no manejada
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex,
            WebRequest request) {

        Map<String, Object> error = buildErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "Error interno del servidor: " + ex.getMessage(),
            request
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Método auxiliar para construir respuesta de error en formato estándar
     */
    private Map<String, Object> buildErrorResponse(String errorType, String message, WebRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", errorType);
        error.put("message", message);
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("path", request.getDescription(false).replace("uri=", ""));
        return error;
    }
}