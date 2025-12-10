package com.squadprisma.notesoccer.orchestration_service.api.exceptions;

import com.squadprisma.notesoccer.orchestration_service.domain.exception.ConflictException;
import com.squadprisma.notesoccer.orchestration_service.infra.exceptionInfra.DownstreamClientException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.squadprisma.notesoccer.orchestration_service.api")
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

    @ExceptionHandler(DownstreamClientException.class)
    public ResponseEntity<?> handleDownstream(DownstreamClientException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", ex.getStatus().value());
        body.put("error", ex.getStatus().getReasonPhrase());
        body.put("code", ex.getCode());
        body.put("message", ex.getMessage());
        body.put("path", ex.getPath());
        if (ex.getViolations() != null && !ex.getViolations().isEmpty()) {
            body.put("violations", ex.getViolations());
        }
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    // 409 - conflito (ConflictException do domínio ou IllegalStateException)
    @ExceptionHandler({ConflictException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    Map<String, Object> handleConflict(RuntimeException ex, HttpServletRequest req) {
        return body(HttpStatus.CONFLICT, ex.getMessage(), ex.getMessage(), req);
    }

    // 400 - bean validation em @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidBody(MethodArgumentNotValidException ex,
                                                                 HttpServletRequest req) {
        var errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(f -> Map.of("field", f.getField(), "message", f.getDefaultMessage()))
                .toList();

        var payload = body(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "Erro de validação nos campos enviados.", req);
        payload.put("errors", errors);

        return ResponseEntity.badRequest().body(payload);
    }

    // 400 - bean validation em @PathVariable/@RequestParam (@Validated no controller)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraint(ConstraintViolationException ex,
                                                                HttpServletRequest req) {
        var errors = ex.getConstraintViolations().stream()
                .map(v -> Map.of("param", v.getPropertyPath().toString(), "message", v.getMessage()))
                .toList();

        var payload = body(HttpStatus.BAD_REQUEST, "CONSTRAINT_VIOLATION",
                "Parâmetros inválidos.", req);
        payload.put("errors", errors);

        return ResponseEntity.badRequest().body(payload);
    }

    // 400 - entrada inválida manual (regra de negócio/guards)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, Object> handleBadRequest(IllegalArgumentException ex, HttpServletRequest req) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getMessage(), req);
    }

    // Propaga mapeamentos vindos do service (ResponseStatusException com status/razão)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex,
                                                                    HttpServletRequest req) {
        // tenta resolver HttpStatus para preencher "error"
        var statusCode = ex.getStatusCode();
        HttpStatus httpStatus = (statusCode instanceof HttpStatus hs)
                ? hs
                : HttpStatus.resolve(statusCode.value());
        if (httpStatus == null) {
            httpStatus = HttpStatus.valueOf(statusCode.value());
        }

        var payload = body(httpStatus,
                ex.getReason(),            // "code" opcional; aqui usamos a reason como código quando fizer sentido
                ex.getReason(),            // "message" também usa a reason (coerente com o service)
                req);

        return ResponseEntity.status(statusCode).body(payload);
    }

    // 500 - fallback (não vaza stacktrace/implementação)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    Map<String, Object> handleUnknown(Exception ex, HttpServletRequest req) {
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "UNEXPECTED_ERROR",
                "Ocorreu um erro inesperado.", req);
    }
}
