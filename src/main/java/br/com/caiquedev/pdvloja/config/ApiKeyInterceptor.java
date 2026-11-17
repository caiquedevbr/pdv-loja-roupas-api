package br.com.caiquedev.pdvloja.config;

import br.com.caiquedev.pdvloja.service.ApiKeyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    public static final String ATRIBUTO_USUARIO_ID = "usuarioSistemaId";

    private final ApiKeyService apiKeyService;

    public ApiKeyInterceptor(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())
                || request.getRequestURI().endsWith("/api/v2/auth/login")) {
            return true;
        }

        request.setAttribute(ATRIBUTO_USUARIO_ID,
                apiKeyService.validar(request.getHeader("X-API-Key")).getId());
        return true;
    }
}
