package com.squadprisma.notesoccer.orchestration_service.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceOrchestration {

    private final JWTVerifier verifier;

    public JwtServiceOrchestration(
            @Value("${security.jwt.secret}") String secret
    ) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm)
                .withIssuer("notesoccer-user-service") // mesmo issuer do user-service
                .build();
    }

    public DecodedJWT validateToken(String token) {
        return verifier.verify(token); // lança exceção se inválido ou expirado
    }
}
