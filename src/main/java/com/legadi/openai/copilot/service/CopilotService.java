package com.legadi.openai.copilot.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;

@Service
public class CopilotService {

    private static final String COPILOT_COMPLETIONS_ENDPOINT = "/inference/chat/completions";
    private static final String COPILOT_MODELS_ENDPOINT = "/catalog/models";

    private final Logger logger = LoggerFactory.getLogger(CopilotService.class);

    private final WebClient copilotWebClient;
    private final ExchangePrinterService printerService;

    public CopilotService(WebClient copilotWebClient,
            ExchangePrinterService printerService) {
        this.copilotWebClient = copilotWebClient;
        this.printerService = printerService;
    }

    public Flux<String> chatCompletion(Map<String, Object> request) {
        Flux<String> response = copilotWebClient.post()
            .uri(COPILOT_COMPLETIONS_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchangeToFlux(res -> {
                printerService.printJsonRequest(res.request(), request);
                printerService.printStartLineAndHeaders(res);
                return res.bodyToFlux(String.class);
            });
        response.subscribe(logger::info);
        return response;
    }

    @Cacheable("models")
    public List<Map<String, Object>> getModels() {
        return copilotWebClient.get()
            .uri(COPILOT_MODELS_ENDPOINT)
            .exchangeToMono(res -> {
                printerService.printRequest(res.request());
                printerService.printStartLineAndHeaders(res);
                return res.bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
            })
            .block();
    }
}
