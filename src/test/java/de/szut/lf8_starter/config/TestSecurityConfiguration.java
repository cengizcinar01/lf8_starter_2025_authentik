package de.szut.lf8_starter.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.util.Collections;

@TestConfiguration
@Profile("it")
public class TestSecurityConfiguration {

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> new Jwt(
                "dummy-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Collections.singletonMap("alg", "none"),
                Collections.singletonMap("sub", "dummy-user")
        );
    }
}