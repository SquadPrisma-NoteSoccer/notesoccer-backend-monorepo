package com.squadprisma.notesoccer.match_service.api.handler;

import com.squadprisma.notesoccer.match_service.domain.exception.ConflictException;
import com.squadprisma.notesoccer.match_service.domain.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> body(HttpStatus status, String code, String message, HttpServletRequest req) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("timestamp", Instant.now().toString());
        m.put("status", status.value());
        m.put("error", status.getReasonPhrase());
        m.put("code", code);
        m.put("message", message);
        m.put("path", req.getRequestURI());
        return m;
    }
    // 409 - conflito (suporta ConflictException e IllegalStateException do Java)
    @ExceptionHandler({ConflictException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    Map<String, Object> handleConflict(RuntimeException ex, HttpServletRequest req) {
        return body(HttpStatus.CONFLICT, ex.getMessage(), ex.getMessage(), req);
    }

    // 404
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Map<String, Object> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return body(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getMessage(), req);
    }

    // 400 - validação bean
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, Object> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst().map(e -> e.getField() + ": " + e.getDefaultMessage())
                .orElse("Validation error");
        return body(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", msg, req);
    }

    // 400 - entrada inválida
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, Object> handleBadRequest(IllegalArgumentException ex, HttpServletRequest req) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getMessage(), req);
    }

    // 500 - fallback
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    Map<String, Object> handleRuntime(RuntimeException ex, HttpServletRequest req) {
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Erro inesperado", req);
    }
}
