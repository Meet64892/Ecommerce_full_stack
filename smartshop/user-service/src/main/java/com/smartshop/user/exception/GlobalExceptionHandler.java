package com.smartshop.user.exception;

import com.smartshop.common.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GlobalExceptionHandler - Centralized REST exception mapping.
 *
 * <h2>Purpose</h2>
 * @RestControllerAdvice captures exceptions from all controllers and transforms them into
 * consistent error payloads. This prevents duplicate try/catch logic in endpoint methods.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Bean Validation flow: constraint violations bubble into this handler.</li>
 *   <li>Centralized mapping: one place controls API error semantics.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Handles domain and validation exceptions thrown by controllers/services.
 *
 * @see ErrorResponse
 * @author SmartShop Team
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles not-found user errors.
     *
     * @param ex thrown exception
     * @return 404 response body
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(final UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of("USER_NOT_FOUND", ex.getMessage(), List.of()));
    }

    /**
     * Handles duplicate email registration attempts.
     *
     * @param ex thrown exception
     * @return 409 conflict response
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(final EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of("EMAIL_ALREADY_EXISTS", ex.getMessage(), List.of()));
    }

    /**
     * Handles DTO validation failures.
     *
     * @param ex validation exception
     * @return 400 with field-level details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(final MethodArgumentNotValidException ex) {
        final List<String> details = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error instanceof FieldError fieldError
                        ? fieldError.getField() + ": " + fieldError.getDefaultMessage()
                        : error.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest().body(ErrorResponse.of("VALIDATION_ERROR", "Request validation failed", details));
    }

    /**
     * Handles path/query constraint violations.
     *
     * @param ex constraint violation exception
     * @return 400 response
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(final ConstraintViolationException ex) {
        final List<String> details = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
        return ResponseEntity.badRequest().body(ErrorResponse.of("CONSTRAINT_VIOLATION", "Constraint violation", details));
    }

    /**
     * Handles uncaught errors to avoid leaking stack details.
     *
     * @param ex unexpected exception
     * @return 500 response body
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(final Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of("INTERNAL_ERROR", ex.getMessage(), List.of()));
    }
}
