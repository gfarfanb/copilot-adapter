package com.legadi.openai.copilot.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.legadi.openai.copilot.dto.ErrorResponse;
import com.legadi.openai.copilot.service.CopilotService;
import com.legadi.openai.copilot.service.CopilotService.ChatResponse;
import com.legadi.openai.copilot.service.CopilotService.ListResponse;

@RestController
@RequestMapping("/v1")
public class OpenAIV1Controller {

    private final CopilotService copilotService;

    public OpenAIV1Controller(CopilotService copilotService) {
        this.copilotService = copilotService;
    }

    @PostMapping(value = "/chat/completions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> chatCompletions(@RequestBody Map<String, Object> request) {
        ChatResponse response = copilotService.chatCompletion(request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .headers(response.headers())
            .body(response.body());
    }

    @GetMapping("/models")
    public ResponseEntity<List<Map<String, Object>>> getModels() {
        ListResponse response = copilotService.getModels();
        return ResponseEntity
            .status(HttpStatus.OK)
            .headers(response.headers())
            .body(response.body());
    }

    @GetMapping("/model")
    public ResponseEntity<Object> getModel(@RequestParam String id) {
        ListResponse response = copilotService.getModels();
        Optional<Map<String, Object>> foundModel = response.body().stream()
            .filter(m -> m.get("id").equals(id))
            .findFirst();

        if (foundModel.isPresent()) {
            return ResponseEntity.ok(foundModel.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                    .error(ErrorResponse.Error.builder()
                        .message("Model not found: " + id)
                        .type("invalid_request_error")
                        .code("model_not_found")
                        .build()
                    )
                    .build());
        }
    }
}
