package com.smartshop.inventory.exception;

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
 * GlobalExceptionHandler - Standardizes inventory-service API errors.
 *
 * <h2>Purpose</h2>
 * Reservation failures should be clear and machine-readable. Centralized handling maps domain and validation errors to
 * a common response shape without repetitive controller code.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>@RestControllerAdvice: Cross-controller exception mapping.</li>
 *   <li>Conflict awareness: Optimistic-locking failures can be translated here in a production extension.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * InventoryController and service methods throw exceptions that this advice converts to ErrorResponse.
 *
 * @see ErrorResponse
 * @author SmartShop Team
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles SmartShop domain exceptions.
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
     * Handles invalid JSON request bodies.
     *
     * @param ex validation exception
     * @return HTTP 400 with field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalid(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream().map(e -> e.getField() + ": " + e.getDefaultMessage()).toList();
        return ResponseEntity.badRequest().body(ErrorResponse.of("VALIDATION_ERROR", "Request validation failed", details));
    }

    /**
     * Handles invalid path or query parameters.
     *
     * @param ex constraint violation exception
     * @return HTTP 400 response
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException ex) {
        List<String> details = ex.getConstraintViolations().stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList();
        return ResponseEntity.badRequest().body(ErrorResponse.of("VALIDATION_ERROR", "Constraint violation", details));
    }
}
