package com.meetime.integracao.config;

import com.meetime.integracao.exception.OAuthException;
import com.meetime.integracao.service.OAuthService;
import com.meetime.integracao.service.TokenStorage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    private final TokenStorage tokenStorage;
    private final OAuthService oAuthService;

    public AuthorizationInterceptor(TokenStorage tokenStorage, OAuthService oAuthService) {
        this.tokenStorage = tokenStorage;
        this.oAuthService = oAuthService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getRequestURI().startsWith("/contacts")) {
            String accessToken = tokenStorage.getAccessToken();

            if (accessToken == null) {
                throw new OAuthException("Access token ausente. Faça login novamente.");
            }
            request.setAttribute("Authorization", "Bearer " + accessToken);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (response.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
            try {
                String newToken = oAuthService.refreshAccessToken();
                tokenStorage.saveTokens(newToken, tokenStorage.getRefreshToken());
            } catch (OAuthException e) {
                System.err.println("Erro ao renovar o token: " + e.getMessage());
            }
        }
    }
}
