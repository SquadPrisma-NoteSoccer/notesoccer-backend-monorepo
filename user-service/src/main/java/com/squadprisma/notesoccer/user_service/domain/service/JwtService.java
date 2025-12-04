package com.squadprisma.notesoccer.user_service.domain.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.squadprisma.notesoccer.user_service.domain.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final long expirationMinutes;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-minutes:60}") long expirationMinutes
    ){
        this.algorithm = Algorithm.HMAC256(secret);;
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(Usuario usuario){
        Instant now = Instant.now();
        Instant expireAt = now.plus(expirationMinutes, ChronoUnit.MINUTES);

        return JWT.create()
                .withIssuer("notesoccer-user-service")
                .withSubject(usuario.getId().toString())
                .withClaim("email", usuario.getEmail())
                .withClaim("nome", usuario.getNome())
                .withClaim("role", usuario.getRole().name())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expireAt))
                .sign(algorithm);
    }
}
