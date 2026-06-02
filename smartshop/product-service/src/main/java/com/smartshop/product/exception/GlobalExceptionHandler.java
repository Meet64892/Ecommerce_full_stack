package com.smartshop.product.exception;

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
 * GlobalExceptionHandler - Standardizes product-service errors.
 *
 * <h2>Purpose</h2>
 * Centralized exception handling keeps controller code small and guarantees every error response has the same JSON
 * shape. Validation details are gathered into a list so UI clients can show field-level feedback.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>@RestControllerAdvice: Applies exception mapping across all REST controllers.</li>
 *   <li>ErrorResponse: Shared common-lib envelope for failures.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Product controllers throw or propagate exceptions and this advice converts them to HTTP responses.
 *
 * @see ErrorResponse
 * @author SmartShop Team
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles known domain exceptions.
     *
     * @param ex business exception
     * @return standardized error response
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBase(BaseException ex) {
        HttpStatus status = ex instanceof ProductNotFoundException ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.of(ex.getCode(), ex.getMessage()));
    }

    /**
     * Handles invalid request bodies.
     *
     * @param ex validation exception from @Valid DTOs
     * @return HTTP 400 with field details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBody(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream().map(e -> e.getField() + ": " + e.getDefaultMessage()).toList();
        return ResponseEntity.badRequest().body(ErrorResponse.of("VALIDATION_ERROR", "Request validation failed", details));
    }

    /**
     * Handles invalid query parameters.
     *
     * @param ex constraint violation exception
     * @return HTTP 400 with violation details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException ex) {
        List<String> details = ex.getConstraintViolations().stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList();
        return ResponseEntity.badRequest().body(ErrorResponse.of("VALIDATION_ERROR", "Constraint violation", details));
    }
}
