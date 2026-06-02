package com.smartshop.product.exception;

import com.smartshop.common.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GlobalExceptionHandler - Product service exception translator.
 *
 * <h2>Purpose</h2>
 * Produces consistent error payloads and status codes from uncaught exceptions.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Controller advice: cross-controller exception handling concern.</li>
 *   <li>Validation normalization: field errors mapped into response details.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Intercepts controller/service exceptions and maps to smartshop common ErrorResponse.
 *
 * @see ErrorResponse
 * @author SmartShop Team
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles not-found domain errors.
     *
     * @param ex missing-resource exception
     * @return 404 payload
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(final ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of("PRODUCT_NOT_FOUND", ex.getMessage(), List.of()));
    }

    /**
     * Handles bean validation failures.
     *
     * @param ex validation exception
     * @return 400 payload
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleValidation(final Exception ex) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("VALIDATION_ERROR", ex.getMessage(), List.of()));
    }

    /**
     * Handles unexpected failures.
     *
     * @param ex uncaught exception
     * @return 500 payload
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(final Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of("INTERNAL_ERROR", ex.getMessage(), List.of()));
    }
}
