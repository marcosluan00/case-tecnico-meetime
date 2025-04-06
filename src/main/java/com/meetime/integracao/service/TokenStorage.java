package com.meetime.integracao.service;

import org.springframework.stereotype.Component;

@Component
public class TokenStorage {
    private String accessToken;

    public void saveAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    // Recuperar o token salvo
    public String getAccessToken() {
        return accessToken;
    }
}
