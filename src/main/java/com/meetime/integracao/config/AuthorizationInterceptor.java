package com.meetime.integracao.config;

import com.meetime.integracao.service.TokenStorage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    private final TokenStorage tokenStorage;

    public AuthorizationInterceptor(TokenStorage tokenStorage) {
        this.tokenStorage = tokenStorage;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Adicionar o token Authorization nas requisições para /contacts
        if (request.getRequestURI().startsWith("/contacts")) {
            String accessToken = tokenStorage.getAccessToken();
            if (accessToken != null) {
                request.setAttribute("Authorization", "Bearer " + accessToken);
            }
        }
        return true;
    }
}
