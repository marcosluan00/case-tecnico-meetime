package com.meetime.integracao.service;

import org.springframework.stereotype.Component;

@Component
public class TokenStorage {
    private String accessToken;
    private String refreshToken;

    public void saveTokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}

