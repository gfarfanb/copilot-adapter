package com.legadi.openai.copilot.service;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.legadi.openai.copilot.config.props.ClientInterceptorProperties;

@Service
public class ExchangePrinterService {

    private final Logger logger = LoggerFactory.getLogger(ExchangePrinterService.class);

    private final Set<String> hiddenHeaders;
    private final ObjectMapper objectMapper;

    public ExchangePrinterService(ClientInterceptorProperties interceptorProperties) {
        this.hiddenHeaders = interceptorProperties.getHiddenHeaders().stream().map(String::toLowerCase)
            .collect(Collectors.toSet());
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public void printRequest(HttpRequest request) {
        logger.info("Request: {} {}", request.getMethod(), request.getURI());
        printHeaders("Request", request.getHeaders());
    }

    public void printJsonRequest(HttpRequest request, Object body) {
        printRequest(request);

        if(body != null) {
            logger.info("Request:");
            try {
                logger.info(objectMapper.writeValueAsString(body));
            } catch(IOException ex) {
                logger.warn("Unable to print request body - {}", ex.getMessage());
            }
        }
    }

    public void printStartLineAndHeaders(ClientResponse response) {
        logger.info("Response status: {}", response.statusCode());
        printHeaders("Response", response.headers().asHttpHeaders());
        logger.info("Response:");
    }

    private void printHeaders(String tag, HttpHeaders headers) {
        for(var entry : headers.entrySet()) {
            if(!hiddenHeaders.contains(entry.getKey().toLowerCase())) {
                logger.info("{} header: {}: {}", tag, entry.getKey(), entry.getValue());
            } else {
                logger.info("{} header: {}: [masked]", tag, entry.getKey());
            }
        }
    }
}
