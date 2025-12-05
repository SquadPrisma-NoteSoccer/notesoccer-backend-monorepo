package com.squadprisma.notesoccer.user_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    /**
     * security.jwt.secret
     */
    private String secret;

    /**
     * security.jwt.expiration-minutes
     */
    private Long expirationMinutes;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getExpirationMinutes() {
        return expirationMinutes;
    }

    public void setExpirationMinutes(Long expirationMinutes) {
        this.expirationMinutes = expirationMinutes;
    }
}
