package br.com.caiquedev.pdvloja.config;

import br.com.caiquedev.pdvloja.service.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;

    public RateLimitInterceptor(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        Object usuarioId = request.getAttribute(ApiKeyInterceptor.ATRIBUTO_USUARIO_ID);
        String identificador = usuarioId == null
                ? "ip:" + request.getRemoteAddr()
                : "usuario:" + usuarioId;
        RateLimitService.Limite limite = rateLimitService.consumir(identificador);
        response.setHeader("X-RateLimit-Limit", String.valueOf(limite.total()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(limite.restante()));
        response.setHeader("X-RateLimit-Reset", String.valueOf(limite.resetEm().getEpochSecond()));
        response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(limite.segundosAteReset()));
        return true;
    }
}
