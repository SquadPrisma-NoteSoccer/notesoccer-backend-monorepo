package com.squadprisma.notesoccer.orchestration_service.api.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handle(MethodArgumentNotValidException ex){
        var errors = ex.getBindingResult().getFieldErrors()
                .stream().map(f -> Map.of("field", f.getField(), "message", f.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(Map.of("errors", errors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handle(IllegalArgumentException ex){
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex,
                                                                    jakarta.servlet.http.HttpServletRequest req) {
        var statusCode = ex.getStatusCode();               // HttpStatusCode
        var httpStatus = (statusCode instanceof org.springframework.http.HttpStatus hs)
                ? hs
                : org.springframework.http.HttpStatus.resolve(statusCode.value());

        String error = (httpStatus != null) ? httpStatus.getReasonPhrase() : "Unknown";

        Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("timestamp", java.time.Instant.now().toString());
        body.put("path", req.getRequestURI());
        body.put("status", statusCode.value());
        body.put("error", error);                          // ← aqui o "reason phrase"
        body.put("message", ex.getReason());               // ← garante $.message

        return org.springframework.http.ResponseEntity.status(statusCode).body(body);
    }
}
