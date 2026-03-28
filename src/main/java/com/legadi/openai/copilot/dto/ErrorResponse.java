package com.legadi.openai.copilot.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ErrorResponse {

    @Builder
    @Getter
    public static class Error {

        private String message;
        private String type;
        private String code;
    }

    private Error error;
}
