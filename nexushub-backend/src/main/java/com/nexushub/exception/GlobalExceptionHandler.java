package com.nexushub.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── Structured Error Response ─────────────────────────────────────

    public record ApiError(
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            String path
    ) {}

    public record ValidationError(
            LocalDateTime timestamp,
            int status,
            String error,
            Map<String, String> fields,
            String path
    ) {}

    // ── Handlers ──────────────────────────────────────────────────────

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex, WebRequest request) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException ex, WebRequest request) {
        log.warn("Unauthorized: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(), request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleValidation(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> fields = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            fields.put(field, message);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ValidationError(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation failed",
                        fields,
                        request.getDescription(false).replace("uri=", "")
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, WebRequest request) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred. Please try again later.", request));
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private ApiError buildError(HttpStatus status, String message, WebRequest request) {
        return new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getDescription(false).replace("uri=", "")
        );
    }
}
