package com.squadprisma.notesoccer.orchestration_service.domain.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final long expirationMinutes;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-minutes:60}") long expirationMinutes
    ) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.expirationMinutes = expirationMinutes;
    }

    public boolean isValid(String token) {
        try {
            JWT.require(algorithm)
                    .withIssuer("notesoccer-user-service")
                    .build()
                    .verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        return JWT.require(algorithm)
                .withIssuer("notesoccer-user-service")
                .build()
                .verify(token)
                .getSubject();
    }

    public String getRoleFromToken(String token) {
        return JWT.require(algorithm)
                .withIssuer("notesoccer-user-service")
                .build()
                .verify(token)
                .getClaim("role")
                .asString();
    }
}
