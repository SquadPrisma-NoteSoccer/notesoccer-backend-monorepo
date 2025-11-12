package com.squadprisma.notesoccer.orchestration_service.infra.feign;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squadprisma.notesoccer.orchestration_service.infra.DownstreamClientException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DownstreamErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.resolve(response.status());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;

        String body = "";
        try (InputStream is = response.body() != null ? response.body().asInputStream() : null) {
            if (is != null) {
                body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                JsonNode root = objectMapper.readTree(body);

                String code = getText(root, "code", "DOWNSTREAM_ERROR");
                String message = getText(root, "message", status.getReasonPhrase());
                String path = getText(root, "path", "");

                List<DownstreamClientException.Violation> violations = new ArrayList<>();
                JsonNode v = root.get("violations");
                if (v != null && v.isArray()) {
                    v.forEach(n -> violations.add(
                            new DownstreamClientException.Violation(
                                    getText(n, "field", null),
                                    getText(n, "message", null)
                            )));
                }

                return new DownstreamClientException(status, code, message, path, violations);
            }
        } catch (Exception ignore) {
            // fallback abaixo
        }

        // Fallback: sem JSON ou parsing falhou
        return new DownstreamClientException(
                status, "DOWNSTREAM_ERROR",
                status + " from downstream (" + response.request().url() + ")", "",
                List.of()
        );
    }

    private static String getText(JsonNode n, String field, String def) {
        return n != null && n.has(field) && !n.get(field).isNull() ? n.get(field).asText() : def;
    }

}
