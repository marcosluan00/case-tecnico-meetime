package com.meetime.integracao.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetime.integracao.config.HubSpotConfig;
import com.meetime.integracao.exception.OAuthException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
public class OAuthService {

    private final HubSpotConfig hubSpotConfig;
    private final RestTemplate restTemplate;
    private final TokenStorage tokenStorage ;

    public OAuthService(HubSpotConfig hubSpotConfig, RestTemplate restTemplate, TokenStorage tokenStorage) {
        this.hubSpotConfig = hubSpotConfig;
        this.restTemplate = restTemplate;
        this.tokenStorage = tokenStorage;
    }

    public String gerarAuthorizationUrl() {
        String state = UUID.randomUUID().toString();

        return UriComponentsBuilder.fromUriString(hubSpotConfig.getAuthorizeUrl())
                .queryParam("client_id", hubSpotConfig.getCliendId())
                .queryParam("redirect_uri", hubSpotConfig.getRedirectUri())
                .queryParam("scope", String.join(" ", hubSpotConfig.getScope()))
                .queryParam("state", state)
                .toUriString();
    }

    public Map<String, String> exchangeCodeForAccessToken(String code) {
        String tokenUrl = hubSpotConfig.getApiUrl() + "/oauth/v1/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", hubSpotConfig.getCliendId());
        requestBody.add("client_secret", hubSpotConfig.getClientSecret());
        requestBody.add("redirect_uri", hubSpotConfig.getRedirectUri());
        requestBody.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new OAuthException("Erro ao autenticar: status " + response.getStatusCode());
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", responseBody.get("access_token").toString());
            tokens.put("refresh_token", responseBody.get("refresh_token").toString());
            return tokens;
        } catch (Exception e) {
            throw new OAuthException("Erro ao trocar o código por tokens.", e);
        }
    }

    public String refreshAccessToken() {
        String tokenUrl = hubSpotConfig.getApiUrl() + "/oauth/v1/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "refresh_token");
        requestBody.add("client_id", hubSpotConfig.getCliendId());
        requestBody.add("client_secret", hubSpotConfig.getClientSecret());
        requestBody.add("refresh_token", tokenStorage.getRefreshToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new OAuthException("Erro ao renovar o token: " + response.getStatusCode());
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);

            String newAccessToken = responseBody.get("access_token").toString();
            tokenStorage.saveTokens(newAccessToken, tokenStorage.getRefreshToken());

            return newAccessToken;
        } catch (Exception e) {
            throw new OAuthException("Erro inesperado ao renovar o token.", e);
        }
    }

}
