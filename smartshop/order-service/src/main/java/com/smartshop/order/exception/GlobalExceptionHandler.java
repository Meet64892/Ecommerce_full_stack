package com.smartshop.order.exception;

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
 * GlobalExceptionHandler - Standardizes order-service error responses.
 *
 * <h2>Purpose</h2>
 * Order workflows involve validation, persistence, and asynchronous events. Central error handling keeps synchronous
 * API errors predictable even when internals are complex.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Controller advice: Converts exceptions after controller/service code throws them.</li>
 *   <li>Validation details: Multiple field errors are returned in one response.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * OrderController delegates to services and this class maps escaped exceptions to ErrorResponse objects.
 *
 * @see ErrorResponse
 * @author SmartShop Team
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles known SmartShop domain exceptions.
     *
     * @param ex domain exception
     * @return standardized error response
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBase(BaseException ex) {
        HttpStatus status = "RESOURCE_NOT_FOUND".equals(ex.getCode()) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.of(ex.getCode(), ex.getMessage()));
    }

    /**
     * Handles invalid request bodies.
     *
     * @param ex validation exception
     * @return HTTP 400 with field details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalid(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream().map(e -> e.getField() + ": " + e.getDefaultMessage()).toList();
        return ResponseEntity.badRequest().body(ErrorResponse.of("VALIDATION_ERROR", "Request validation failed", details));
    }

    /**
     * Handles invalid path/query values.
     *
     * @param ex constraint violation exception
     * @return HTTP 400 with details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException ex) {
        List<String> details = ex.getConstraintViolations().stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList();
        return ResponseEntity.badRequest().body(ErrorResponse.of("VALIDATION_ERROR", "Constraint violation", details));
    }
}
