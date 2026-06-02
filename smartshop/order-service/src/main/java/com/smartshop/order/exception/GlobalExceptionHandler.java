package com.smartshop.order.exception;

import com.smartshop.common.dto.ErrorResponse;
import com.smartshop.common.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/** GlobalExceptionHandler for Order Service */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex, HttpServletRequest request) {
        log.warn("Exception at {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus())
                .body(ErrorResponse.of(ex.getHttpStatus().value(), ex.getErrorCode(), ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> violations = ex.getBindingResult().getFieldErrors().stream()
                .map((FieldError f) -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(ErrorResponse.validationError(request.getRequestURI(), violations));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_ERROR", "An unexpected error occurred", request.getRequestURI()));
    }
}
