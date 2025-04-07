package com.meetime.integracao.service;

import com.meetime.integracao.config.HubSpotConfig;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ContactService {

    private final RestTemplate restTemplate;
    private final TokenStorage tokenStorage;
    private final HubSpotConfig hubSpotConfig;

    public ContactService(RestTemplate restTemplate, TokenStorage tokenStorage, HubSpotConfig hubSpotConfig) {
        this.restTemplate = restTemplate;
        this.tokenStorage = tokenStorage;
        this.hubSpotConfig = hubSpotConfig;
    }

    public String createContact(Map<String, Object> contactData) {
        String accessToken = tokenStorage.getAccessToken();
        String url = this.hubSpotConfig.getApiUrl()+"/crm/v3/objects/contacts";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(contactData, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }

    public Map<String, Object> getAllContacts() {
        String accessToken = tokenStorage.getAccessToken();
        String url = this.hubSpotConfig.getApiUrl()+"/crm/v3/objects/contacts";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        return response.getBody();
    }
}

