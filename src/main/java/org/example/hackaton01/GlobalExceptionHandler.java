package org.example.hackaton01;

import com.azure.core.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación (@Valid) - para ReportRequest
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Formato estándar según documento del hackathon
        Map<String, Object> response = new HashMap<>();
        response.put("error", "BAD_REQUEST");
        response.put("message", "Validación fallida");
        response.put("details", errors);
        response.put("timestamp", java.time.LocalDateTime.now());

        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de seguridad (permisos de sucursal)
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, String>> handleSecurityException(SecurityException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "FORBIDDEN");
        error.put("message", ex.getMessage());
        error.put("timestamp", java.time.LocalDateTime.now().toString());
        return new ResponseEntity(error, HttpStatus.FORBIDDEN);
    }

    /**
     * Maneja excepciones generales de runtime
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "BAD_REQUEST");
        error.put("message", ex.getMessage());
        error.put("timestamp", java.time.LocalDateTime.now().toString());
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones cuando no se encuentra un recurso
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "NOT_FOUND");
        error.put("message", ex.getMessage());
        error.put("timestamp", java.time.LocalDateTime.now().toString());
        return new ResponseEntity(error, HttpStatus.NOT_FOUND);
    }
}