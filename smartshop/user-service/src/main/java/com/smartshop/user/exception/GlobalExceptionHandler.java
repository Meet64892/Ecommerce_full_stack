package com.smartshop.user.exception;

import com.smartshop.common.dto.ErrorResponse;
import com.smartshop.common.exception.BaseException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * GlobalExceptionHandler - Centralizes user-service error responses.
 *
 * <h2>Purpose</h2>
 * @RestControllerAdvice applies exception handling across all controllers so every endpoint returns the same error
 * shape. Centralization keeps controllers focused on request flow instead of repetitive try/catch blocks.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Controller advice: Cross-cutting MVC behavior applied after exceptions escape controllers.</li>
 *   <li>Validation flow: Bean Validation failures become structured ErrorResponse details.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Controllers and services throw exceptions; this advice converts them to HTTP responses for the gateway/client.
 *
 * @see ErrorResponse
 * @author SmartShop Team
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles known domain exceptions with appropriate HTTP status codes.
     *
     * @param ex domain exception carrying a stable error code
     * @return standardized error response
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        HttpStatus status = ex instanceof UserNotFoundException ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.of(ex.getCode(), ex.getMessage()));
    }

    /**
     * Handles invalid request bodies produced by @Valid on DTO records.
     *
     * @param ex validation exception from Spring MVC
     * @return HTTP 400 with all field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest().body(ErrorResponse.of("VALIDATION_ERROR", "Request validation failed", details));
    }

    /**
     * Handles validation failures that occur on path or query parameters.
     *
     * @param ex constraint violation exception from method validation
     * @return HTTP 400 with violation details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> details = ex.getConstraintViolations().stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList();
        return ResponseEntity.badRequest().body(ErrorResponse.of("VALIDATION_ERROR", "Constraint violation", details));
    }
}
