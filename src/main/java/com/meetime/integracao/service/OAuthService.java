package com.meetime.integracao.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetime.integracao.config.HubSpotConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;


@Service
public class OAuthService {

    private final HubSpotConfig hubSpotConfig;
    private final RestTemplate restTemplate;

    public OAuthService(HubSpotConfig hubSpotConfig, RestTemplate restTemplate) {
        this.hubSpotConfig = hubSpotConfig;
        this.restTemplate = restTemplate;
    }

    public String gerarAuthorizationUrl() {
        String state = UUID.randomUUID().toString();

        String authorizationUrl = UriComponentsBuilder.fromHttpUrl(hubSpotConfig.getAuthorizeUrl())
                .queryParam("client_id", hubSpotConfig.getCliendId())
                .queryParam("redirect_uri", hubSpotConfig.getRedirectUri())
                .queryParam("scope", String.join(" ", hubSpotConfig.getScope()))
                .queryParam("state", state)
                .toUriString();

        return authorizationUrl;
    }


    public String exchangeCodeForAccessToken(String code) {
        String tokenUrl = "https://api.hubapi.com/oauth/v1/token";

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

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);

                return responseBody.get("access_token") != null ? responseBody.get("access_token").toString() : null;
            } else {
                System.err.println("API retornou erro com status: " + response.getStatusCode());
                System.err.println("Resposta (Body): " + response.getBody());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Erro ao trocar código por token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
