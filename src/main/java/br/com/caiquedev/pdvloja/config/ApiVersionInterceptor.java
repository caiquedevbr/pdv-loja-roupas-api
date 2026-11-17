package br.com.caiquedev.pdvloja.config;

import br.com.caiquedev.pdvloja.exception.InvalidRequestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiVersionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        if (!"2".equals(request.getHeader("X-API-Version"))) {
            throw new InvalidRequestException(
                    "Os endpoints /api/v2 exigem o cabeçalho X-API-Version com o valor 2.");
        }
        return true;
    }
}
