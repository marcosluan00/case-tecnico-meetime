package com.meetime.integracao.controller;

import com.meetime.integracao.service.OAuthService;
import com.meetime.integracao.service.TokenStorage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public String processCallback(@RequestParam("code") String code) {
        String accessToken = oAuthService.exchangeCodeForAccessToken(code);
        if (accessToken != null) {
            tokenStorage.saveAccessToken(accessToken);
            return "Acesso concedido!";
        }
        return "Acesso negado!";
    }
}

