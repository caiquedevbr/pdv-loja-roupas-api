package br.com.caiquedev.pdvloja.config;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    private final String origensConfiguradas;

    public CorsConfig(@Value("${app.cors.allowed-origins:http://localhost:5173}") String origensConfiguradas) {
        this.origensConfiguradas = origensConfiguradas;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(origens())
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("Content-Type", "Accept", "X-API-Version", "X-API-Key", "X-Idempotency-Key")
                        .exposedHeaders("Location", "Idempotent-Replayed", "X-RateLimit-Limit",
                                "X-RateLimit-Remaining", "X-RateLimit-Reset", "Retry-After")
                        .maxAge(3600);
            }
        };
    }

    private String[] origens() {
        return java.util.Arrays.stream(origensConfiguradas.split(","))
                .map(String::trim)
                .filter(origem -> !origem.isEmpty())
                .toArray(String[]::new);
    }
}
