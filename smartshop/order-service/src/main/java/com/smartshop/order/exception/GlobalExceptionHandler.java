package com.smartshop.order.exception;

import com.smartshop.common.dto.ErrorResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GlobalExceptionHandler - Centralized exception mapping for order APIs.
 *
 * <h2>Purpose</h2>
 * Keeps controller methods focused on business behavior by centralizing HTTP error conversion.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Advice pattern: cross-cutting error handling in one place.</li>
 *   <li>Consistent payload shape: shared ErrorResponse DTO.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Handles exceptions thrown by controller/service/saga flows.
 *
 * @see ErrorResponse
 * @author SmartShop Team
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles missing order exceptions.
     *
     * @param ex exception
     * @return 404 response
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(final OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of("ORDER_NOT_FOUND", ex.getMessage(), List.of()));
    }

    /**
     * Handles generic uncaught exceptions.
     *
     * @param ex exception
     * @return 500 response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(final Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of("INTERNAL_ERROR", ex.getMessage(), List.of()));
    }
}
