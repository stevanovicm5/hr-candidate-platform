package com.intens.hr_platform.exception;

import jakarta.validation.ConstraintViolationException;
import tools.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateResource(DuplicateResourceException ex){
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException ex){
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex){
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid request format");

        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalidFormatEx) {
            String fieldPath = invalidFormatEx.getPath().getLast().getPropertyName();
            Class<?> targetType = invalidFormatEx.getTargetType();

            if (targetType.equals(LocalDate.class)) {
                error.put("message", "Field '" + fieldPath + "' must be in format: yyyy-MM-dd");
            } else if (targetType.equals(Long.class)) {
                error.put("message", "Field '" + fieldPath + "' must be a number");
            }
            else {
                error.put("message", "Field '" + fieldPath + "' has invalid value: " + invalidFormatEx.getValue());
            }
        } else {
            error.put("message", "Malformed JSON request - check JSON syntax and field types");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations()
                .forEach(cv -> {
                    String field = cv.getPropertyPath().toString();
                    field = field.substring(field.lastIndexOf('.') + 1);
                    errors.put(field, cv.getMessage());
                });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}