package com.smartshop.product.exception;

import com.smartshop.common.dto.ErrorResponse;
import com.smartshop.common.exception.BaseException;
import com.smartshop.common.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * GlobalExceptionHandler - Centralized error mapping for the product-service.
 *
 * <h2>Purpose</h2>
 * Converts exceptions into the shared {@link ErrorResponse} so the product API's
 * error contract matches the rest of the platform.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@code @RestControllerAdvice} intercepts exceptions across all
 *       controllers; each {@code @ExceptionHandler} maps a type to a status.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Applies to every controller in this service.
 *
 * @author SmartShop Team
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @param ex not-found exception
     * @return 404 response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(ex.getErrorCode(), ex.getMessage()));
    }

    /**
     * @param ex validation exception from {@code @Valid}
     * @return 400 response with per-field details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .toList();
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("VALIDATION_ERROR", "Request validation failed", details));
    }

    /**
     * @param ex any other domain exception
     * @return 400 response with the stable code
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBase(BaseException ex) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(ex.getErrorCode(), ex.getMessage()));
    }

    /**
     * @param ex any unhandled exception
     * @return 500 response (details logged, not leaked)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of("INTERNAL_ERROR", "An unexpected error occurred"));
    }

    private String formatFieldError(FieldError fe) {
        return fe.getField() + ": " + fe.getDefaultMessage();
    }
}
