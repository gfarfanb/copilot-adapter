package com.legadi.openai.copilot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientException;

import com.legadi.openai.copilot.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(WebClientException.class)
    public ResponseEntity<ErrorResponse> handleRestClientResponseException(WebClientException ex) {
        logger.error("Error getting Web client response", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.builder()
                .error(ErrorResponse.Error.builder()
                    .message(ex.getMessage())
                    .type("invalid_request_error")
                    .code("invalid_request")
                    .build()
                )
                .build()
            );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        logger.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.builder()
                .error(ErrorResponse.Error.builder()
                    .message("Internal server error: " + ex.getMessage())
                    .type("internal_error")
                    .code("internal_error")
                    .build()
                )
                .build()
            );
    }
}
