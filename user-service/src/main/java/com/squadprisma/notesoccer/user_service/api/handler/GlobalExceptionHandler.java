package com.squadprisma.notesoccer.user_service.api.handler;

import com.squadprisma.notesoccer.user_service.domain.exception.EmailAlreadyInUseException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ===================== Helpers ===================== */

    private Map<String, Object> base(int status, String error, String code, String message, String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status);
        body.put("error", error);
        body.put("code", code);
        body.put("message", message);
        body.put("path", path);
        return body;
    }

    private Map<String, Object> withViolations(Map<String, Object> base, List<Map<String, String>> violations) {
        base.put("violations", violations);
        return base;
    }

    /* =============== 409 – Regras de negócio =============== */

    @ExceptionHandler(EmailAlreadyInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    Map<String, Object> handleEmailEmUso(EmailAlreadyInUseException ex, HttpServletRequest req){
        return base(409, "Conflict", "EMAIL_ALREADY_EXISTS",
                "E-mail já cadastrado.", req.getRequestURI());
    }

    /* =============== 409 – Integridade de dados (SQL) =============== */

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    Map<String, Object> handleUniqueViolation(DataIntegrityViolationException ex, HttpServletRequest req) {
        String code   = "DATA_INTEGRITY_VIOLATION";
        String msg    = "Violação de integridade de dados.";
        String detail = null;

        Throwable root = NestedExceptionUtils.getMostSpecificCause(ex);
        if (root instanceof PSQLException psql && psql.getServerErrorMessage() != null) {
            String sqlState   = psql.getSQLState();                 // 23505/23503/23502...
            String constraint = psql.getServerErrorMessage().getConstraint();
            detail            = psql.getServerErrorMessage().getDetail();

            if ("23505".equals(sqlState)) {                         // unique_violation
                code = "EMAIL_ALREADY_EXISTS";
                msg  = "E-mail já cadastrado.";
            } else if ("23503".equals(sqlState)) {                  // foreign_key_violation
                code = "FOREIGN_KEY_VIOLATION";
                msg  = "Registro relacionado não encontrado (violação de chave estrangeira).";
            } else if ("23502".equals(sqlState)) {                  // not_null_violation
                code = "NOT_NULL_VIOLATION";
                msg  = "Campo obrigatório ausente (violação de NOT NULL).";
            }

            // se quiser distinguir por constraint:
            if ("uk_users_email".equalsIgnoreCase(constraint)) {
                code = "EMAIL_ALREADY_EXISTS";
                msg  = "E-mail já cadastrado.";
            }
        }

        Map<String, Object> body = base(409, "Conflict", code, msg, req.getRequestURI());
        if (detail != null) body.put("detail", detail);
        return body;
    }

    /* =============== 400 – Bean Validation (body) =============== */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, Object> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<Map<String, String>> violations = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(this::toViolation)
                .toList();

        return withViolations(
                base(400, "Bad Request", "VALIDATION_ERROR",
                        "Erro de validação nos campos.", req.getRequestURI()),
                violations
        );
    }

    private Map<String, String> toViolation(FieldError fe) {
        Map<String, String> v = new LinkedHashMap<>();
        v.put("field", fe.getField());
        v.put("message", fe.getDefaultMessage());
        return v;
    }

    /* =============== 400 – ConstraintViolation (path/query) =============== */

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, Object> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        List<Map<String, String>> violations = ex.getConstraintViolations().stream()
                .map(this::toViolation)
                .toList();

        return withViolations(
                base(400, "Bad Request", "CONSTRAINT_VIOLATION",
                        "Parâmetros inválidos.", req.getRequestURI()),
                violations
        );
    }

    private Map<String, String> toViolation(ConstraintViolation<?> cv) {
        Map<String, String> v = new LinkedHashMap<>();
        v.put("field", String.valueOf(cv.getPropertyPath()));
        v.put("message", cv.getMessage());
        return v;
    }

    /* =============== 400 – JSON mal-formado / tipo errado =============== */

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, Object> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return base(400, "Bad Request", "MALFORMED_JSON",
                "Corpo da requisição inválido ou mal formatado.", req.getRequestURI());
    }

    /* =============== 405 – Método não suportado =============== */

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    Map<String, Object> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return base(405, "Method Not Allowed", "METHOD_NOT_ALLOWED",
                "Método HTTP não suportado para este endpoint.", req.getRequestURI());
    }

    /* =============== 404 – Recurso não encontrado (ver config abaixo) =============== */

    // spring.mvc.throw-exception-if-no-handler-found=true
    // spring.web.resources.add-mappings=false
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNoResource(org.springframework.web.servlet.resource.NoResourceFoundException ex,
                                                HttpServletRequest req) {
        return base(404, "Not Found", "RESOURCE_NOT_FOUND",
                "Recurso não encontrado.", req.getRequestURI());
    }

    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Map<String, Object> handleNoHandler(org.springframework.web.servlet.NoHandlerFoundException ex,
                                        jakarta.servlet.http.HttpServletRequest req) {
        return base(404, "Not Found", "RESOURCE_NOT_FOUND",
                "Recurso não encontrado.", req.getRequestURI());
    }

    /* =============== 500 – Fallback genérico =============== */

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    Map<String, Object> handleGeneric(Exception ex, HttpServletRequest req) {
        return base(500, "Internal Server Error", "UNEXPECTED_ERROR",
                "Ocorreu um erro inesperado.", req.getRequestURI());
    }
}
