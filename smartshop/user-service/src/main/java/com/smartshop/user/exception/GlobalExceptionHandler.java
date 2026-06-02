package com.smartshop.user.exception;

import com.smartshop.common.dto.ErrorResponse;
import com.smartshop.common.exception.BaseException;
import com.smartshop.common.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * GlobalExceptionHandler - Centralized HTTP error mapping for the user-service.
 *
 * <h2>Purpose</h2>
 * Instead of try/catch in every controller, {@code @RestControllerAdvice}
 * intercepts exceptions thrown anywhere in the request and converts them into a
 * consistent {@link ErrorResponse}. This keeps controllers focused on the happy
 * path and guarantees a uniform error contract.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>@RestControllerAdvice</b>: {@code @ControllerAdvice} +
 *       {@code @ResponseBody}; each {@code @ExceptionHandler} maps an exception
 *       type to an HTTP status + body.</li>
 *   <li><b>Validation flow</b>: {@code @Valid} failures raise
 *       {@code MethodArgumentNotValidException}; we unpack its field errors into
 *       the {@code details} list so the client can highlight each bad field.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Applies to all controllers in this service; the gateway forwards these bodies
 * to clients unchanged.
 *
 * @author SmartShop Team
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles "not found" errors -> HTTP 404.
     *
     * @param ex the not-found exception
     * @return a 404 response with the resource error code
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(ex.getErrorCode(), ex.getMessage()));
    }

    /**
     * Handles duplicate-email conflicts -> HTTP 409.
     *
     * @param ex the conflict exception
     * @return a 409 response
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleConflict(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(ex.getErrorCode(), ex.getMessage()));
    }

    /**
     * Handles failed logins -> HTTP 401. We intentionally return a generic
     * message so attackers cannot tell whether the email or the password was
     * wrong (avoids user enumeration).
     *
     * @param ex the bad-credentials exception
     * @return a 401 response
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of("INVALID_CREDENTIALS", "Invalid email or password"));
    }

    /**
     * Handles bean-validation failures -> HTTP 400 with per-field details.
     *
     * @param ex the validation exception raised by {@code @Valid}
     * @return a 400 response listing each field error
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
     * Catch-all for our own domain exceptions not handled above -> HTTP 400.
     *
     * @param ex the base exception
     * @return a 400 response carrying the stable error code
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBase(BaseException ex) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(ex.getErrorCode(), ex.getMessage()));
    }

    /**
     * Final safety net for anything unexpected -> HTTP 500. We log the full
     * stack trace server-side but never leak internals to the client.
     *
     * @param ex any unhandled exception
     * @return a generic 500 response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of("INTERNAL_ERROR", "An unexpected error occurred"));
    }

    /**
     * @param fe a single field error
     * @return a "field: message" string for the details list
     */
    private String formatFieldError(FieldError fe) {
        return fe.getField() + ": " + fe.getDefaultMessage();
    }
}
