package com.squadprisma.notesoccer.orchestration_service.infra.exceptionInfra;

import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.Map;

public class DownstreamHttpException extends RuntimeException {
    private final HttpStatus status;
    private final String body;
    private final Map<String, Collection<String>> headers;
    private final String service;

    public DownstreamHttpException(HttpStatus status,
                                   String body,
                                   Map<String, Collection<String>> headers,
                                   String service) {
        super("Downstream error from " + service + " - status=" + status.value());
        this.status = status;
        this.body = body;
        this.headers = headers;
        this.service = service;
    }

    public HttpStatus getStatus() { return status; }
    public String getBody() { return body; }
    public Map<String, Collection<String>> getHeaders() { return headers; }
    public String getService() { return service; }
}
