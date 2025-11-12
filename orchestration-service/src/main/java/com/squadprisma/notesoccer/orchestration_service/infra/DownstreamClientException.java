package com.squadprisma.notesoccer.orchestration_service.infra;

import org.springframework.http.HttpStatus;

import java.util.List;

public class DownstreamClientException extends RuntimeException{
    private final HttpStatus status;
    private final String code;
    private final String path;
    private final List<Violation> violations;

    public record Violation(String field, String message) {}

    public DownstreamClientException(
            HttpStatus status, String code, String message, String path, List<Violation> violations
    ) {
        super(message);
        this.status = status;
        this.code = code;
        this.path = path;
        this.violations = violations;
    }

    public HttpStatus getStatus() { return status; }
    public String getCode() { return code; }
    public String getPath() { return path; }
    public List<Violation> getViolations() { return violations; }
}
