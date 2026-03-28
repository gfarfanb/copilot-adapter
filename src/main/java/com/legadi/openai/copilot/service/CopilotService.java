package com.legadi.openai.copilot.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Service
public class CopilotService {

    private final RestClient copilotRestClient;

    private static final String COPILOT_COMPLETIONS_ENDPOINT = "/inference/chat/completions";
    private static final String COPILOT_MODELS_ENDPOINT = "/catalog/models";

    public CopilotService(RestClient copilotRestClient) {
        this.copilotRestClient = copilotRestClient;
    }

    public ChatResponse chatCompletion(Map<String, Object> request) {
        AtomicReference<HttpHeaders> headers = new AtomicReference<>();
        StreamingResponseBody response = out -> {
            copilotRestClient.post()
                .uri(COPILOT_COMPLETIONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange((req, res) -> {
                    headers.set(res.getHeaders());
                    res.getBody().transferTo(out);
                    out.flush();
                    return null;
                });
        };
        return new ChatResponse(response, headers.get());
    }

    @Cacheable("models")
    public ListResponse getModels() {
        ResponseEntity<List<Map<String, Object>>> response = copilotRestClient.get()
            .uri(COPILOT_MODELS_ENDPOINT)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
        return new ListResponse(response.getBody(), response.getHeaders());
    }

    public record ListResponse(List<Map<String, Object>> body, HttpHeaders headers)
        implements Serializable { }

    public record ChatResponse(StreamingResponseBody body, HttpHeaders headers) { }
}
