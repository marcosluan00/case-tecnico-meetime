package com.meetime.integracao.controller;

import com.meetime.integracao.exception.OAuthException;
import com.meetime.integracao.service.OAuthService;
import com.meetime.integracao.service.TokenStorage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/oauth")
public class OAuthController {
    private final OAuthService oAuthService;
    private final TokenStorage tokenStorage;

    public OAuthController(OAuthService oAuthService, TokenStorage tokenStorage) {
        this.oAuthService = oAuthService;
        this.tokenStorage = tokenStorage;
    }

    @GetMapping("/authorize")
    public String generateAuthorizationUrl() {
        return oAuthService.gerarAuthorizationUrl();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> processCallback(@RequestParam("code") String code) {
        try {
            Map<String, String> tokens = oAuthService.exchangeCodeForAccessToken(code);

            String accessToken = tokens.get("access_token");
            String refreshToken = tokens.get("refresh_token");

            if (accessToken != null && refreshToken != null) {
                tokenStorage.saveTokens(accessToken, refreshToken);
                System.out.println("Tokens salvos com sucesso!");
                return ResponseEntity.ok("Acesso concedido!");
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tokens ausentes. Acesso negado!");
        } catch (OAuthException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erro durante o processo de autenticação: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado. Por favor, tente novamente.");
        }
    }
}

