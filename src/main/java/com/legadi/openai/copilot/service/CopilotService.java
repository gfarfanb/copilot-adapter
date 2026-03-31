package com.legadi.openai.copilot.service;

import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;

@Service
public class CopilotService {

    private static final String COPILOT_COMPLETIONS_ENDPOINT = "/inference/chat/completions";
    private static final String COPILOT_EMBEDDINGS_ENDPOINT = "/inference/embeddings";
    private static final String COPILOT_MODELS_ENDPOINT = "/catalog/models";

    private final WebClient copilotWebClient;
    private final ExchangePrinterService printerService;

    public CopilotService(WebClient copilotWebClient,
            ExchangePrinterService printerService) {
        this.copilotWebClient = copilotWebClient;
        this.printerService = printerService;
    }

    public Flux<String> chatCompletion(Map<String, Object> request) {
        return copilotWebClient.post()
            .uri(COPILOT_COMPLETIONS_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchangeToFlux(res -> {
                if (res.statusCode().is2xxSuccessful()) {
                    printerService.printClientResponseAndJsonBody(res, request);
                    return res.bodyToFlux(String.class);
                } else {
                    return Flux.from(res.createError());
                }
            })
            .doOnNext(printerService::printPlainResponse);
    }

    @Cacheable("models")
    public List<Map<String, Object>> getModels() {
        var listTypeReference = new ParameterizedTypeReference<List<Map<String, Object>>>() {};
        return copilotWebClient.get()
            .uri(COPILOT_MODELS_ENDPOINT)
            .exchangeToMono(res -> {
                if (res.statusCode().is2xxSuccessful()) {
                    printerService.printClientResponse(res);
                    return res.bodyToMono(listTypeReference);
                } else {
                    return res.createError();
                }
            })
            .doOnNext(printerService::printJsonResponse)
            .block();
    }

    public Map<String, Object> embeddings(Map<String, Object> request) {
        var objectTypeReference = new ParameterizedTypeReference<Map<String, Object>>() {};
        return copilotWebClient.post()
            .uri(COPILOT_EMBEDDINGS_ENDPOINT)
            .bodyValue(request)
            .exchangeToMono(res -> {
                if (res.statusCode().is2xxSuccessful()) {
                    printerService.printClientResponseAndJsonBody(res, request);
                    return res.bodyToMono(objectTypeReference);
                } else {
                    return res.createError();
                }
            })
            .doOnNext(printerService::printJsonResponse)
            .block();
    }
}
