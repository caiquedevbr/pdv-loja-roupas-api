package br.com.caiquedev.pdvloja.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiKeyConfig implements WebMvcConfigurer {

    private final ApiKeyInterceptor interceptor;
    private final ApiVersionInterceptor apiVersionInterceptor;
    private final RateLimitInterceptor rateLimitInterceptor;

    public ApiKeyConfig(ApiKeyInterceptor interceptor,
                        ApiVersionInterceptor apiVersionInterceptor,
                        RateLimitInterceptor rateLimitInterceptor) {
        this.interceptor = interceptor;
        this.apiVersionInterceptor = apiVersionInterceptor;
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiVersionInterceptor).addPathPatterns("/api/v2/**").order(0);
        registry.addInterceptor(interceptor).addPathPatterns("/api/v2/**").order(1);
        registry.addInterceptor(rateLimitInterceptor).addPathPatterns("/api/v2/**").order(2);
    }
}
