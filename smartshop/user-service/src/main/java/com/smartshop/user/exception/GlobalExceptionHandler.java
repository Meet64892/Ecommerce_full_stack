package com.smartshop.user.exception;

import com.smartshop.common.dto.ErrorResponse;
import com.smartshop.common.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler - Centralized Exception Handling for User Service
 *
 * <h2>Purpose</h2>
 * Catches ALL exceptions thrown by controllers and service methods, converting
 * them into standardized ErrorResponse objects. Without this, Spring Boot would
 * return its default white-label error page or a raw exception stack trace —
 * both unacceptable for a production API.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>@RestControllerAdvice: Combines @ControllerAdvice (applies to all controllers)
 *       with @ResponseBody (return types are serialized to JSON).
 *       @ExceptionHandler methods in this class intercept exceptions thrown anywhere
 *       in the request-response cycle for controllers in this application.</li>
 *   <li>Exception Ordering: Spring evaluates @ExceptionHandler methods in order.
 *       More specific exceptions should be handled BEFORE general ones.
 *       BaseException (our custom hierarchy) is handled before Exception (catch-all).</li>
 *   <li>MethodArgumentNotValidException: Thrown by Spring when @Valid fails on a
 *       request body. Contains a BindingResult with all field violation details.
 *       We extract them into a flat list of "fieldName: message" strings.</li>
 *   <li>Never expose stack traces: For 5xx errors, return a generic message.
 *       Stack traces reveal your implementation details — a security risk.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles all our custom SmartShop exceptions (BaseException subclasses).
     * These exceptions carry their own HTTP status and error code, so we just
     * extract and forward them.
     *
     * @param ex      the caught exception
     * @param request the HTTP request (for extracting the path)
     * @return ResponseEntity with the appropriate HTTP status and ErrorResponse body
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(
            BaseException ex, HttpServletRequest request) {
        log.warn("Business exception: {} at {}", ex.getMessage(), request.getRequestURI());
        ErrorResponse error = ErrorResponse.of(
                ex.getHttpStatus().value(),
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(ex.getHttpStatus()).body(error);
    }

    /**
     * Handles Jakarta Bean Validation failures (@Valid annotation on @RequestBody).
     * Collects ALL field validation errors into a list for a comprehensive response.
     * This prevents the "fix one field, find another error" frustration for API consumers.
     *
     * @param ex      the validation exception containing all field errors
     * @param request the HTTP request
     * @return 400 Bad Request with list of validation violations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        // Extract all field-level violations as "fieldName: message" strings
        List<String> violations = ex.getBindingResult().getFieldErrors().stream()
                .map((FieldError fieldError) ->
                        fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        log.warn("Validation failed at {}: {}", request.getRequestURI(), violations);

        ErrorResponse error = ErrorResponse.validationError(request.getRequestURI(), violations);
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handles Spring Security's BadCredentialsException (wrong password).
     * Returns 401 Unauthorized — the request is unauthenticated.
     *
     * @param ex      the credentials exception
     * @param request the HTTP request
     * @return 401 Unauthorized with a generic credentials error message
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        log.warn("Authentication failed at {}: {}", request.getRequestURI(), ex.getMessage());
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(),
                "AUTHENTICATION_FAILED",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Catch-all handler for unexpected exceptions.
     * Returns 500 Internal Server Error with a GENERIC message (never expose details).
     * The real exception is logged with full stack trace for debugging.
     *
     * @param ex      the unexpected exception
     * @param request the HTTP request
     * @return 500 Internal Server Error with a generic message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        // Log the full stack trace for debugging — but don't send it to the client
        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI()
        );
        return ResponseEntity.internalServerError().body(error);
    }
}
