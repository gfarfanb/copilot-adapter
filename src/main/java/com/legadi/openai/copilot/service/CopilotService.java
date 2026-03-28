package com.legadi.openai.copilot.service;

import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CopilotService {

    private final RestClient copilotRestClient;

    private static final String COPILOT_COMPLETIONS_ENDPOINT = "/inference/chat/completions";
    private static final String COPILOT_MODELS_ENDPOINT = "/catalog/models";

    public CopilotService(RestClient copilotRestClient) {
        this.copilotRestClient = copilotRestClient;
    }

    public Map<String, Object> chatCompletion(Map<String, Object> request) {
        return copilotRestClient.post()
            .uri(COPILOT_COMPLETIONS_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    @Cacheable("models")
    public List<Map<String, Object>> getModels() {
        return copilotRestClient.get()
            .uri(COPILOT_MODELS_ENDPOINT)
            .retrieve()
            .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
    }
}
