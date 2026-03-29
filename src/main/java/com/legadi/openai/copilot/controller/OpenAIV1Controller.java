package com.legadi.openai.copilot.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.legadi.openai.copilot.service.CopilotService;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v1")
public class OpenAIV1Controller {

    private final CopilotService copilotService;

    public OpenAIV1Controller(CopilotService copilotService) {
        this.copilotService = copilotService;
    }

    @PostMapping(value = "/chat/completions", produces = { MediaType.TEXT_EVENT_STREAM_VALUE })
    public ResponseEntity<Flux<String>> chatCompletions(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(copilotService.chatCompletion(request));
    }

    @PostMapping("/embeddings")
    public ResponseEntity<Map<String, Object>> embeddings(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(copilotService.embeddings(request));
    }

    @GetMapping("/models")
    public ResponseEntity<List<Map<String, Object>>> getModels() {
        return ResponseEntity.ok(copilotService.getModels());
    }
}
