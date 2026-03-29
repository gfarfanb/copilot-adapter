package com.legadi.openai.copilot.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Set;
import java.util.UUID;
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

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private final Set<String> hiddenHeaders;
    private final String prefix;

    public ExchangePrinterService(ClientInterceptorProperties interceptorProperties) {
        this.hiddenHeaders = interceptorProperties.getHiddenHeaders().stream().map(String::toLowerCase)
            .collect(Collectors.toSet());
        this.prefix = interceptorProperties.getPrefix();
    }

    public void printClientResponse(ClientResponse res) {
        printClientResponse(UUID.randomUUID(), res);
    }

    public void printClientResponse(UUID correlationId, ClientResponse res) {
        HttpRequest request = res.request();
        infoRequest(correlationId, "%s %s", request.getMethod(), request.getURI());
        printHeaders(correlationId, request.getHeaders(), true);

        infoResponse(correlationId, "Status: %s", res.statusCode());
        printHeaders(correlationId, res.headers().asHttpHeaders(), false);
    }

    public void printClientResponseAndJsonBody(ClientResponse res, Object jsonBody) {
        UUID correlationId = UUID.randomUUID();

        printClientResponse(correlationId, res);
        printJsonObject(correlationId, jsonBody, true);
    }

    public void printPlainResponse(String plainBody) {
        printPlain(UUID.randomUUID(), plainBody, false);
    }

    public void printJsonResponse(Object jsonBody) {
        printJsonObject(UUID.randomUUID(), jsonBody, false);
    }

    private void printPlain(UUID correlationId, String plainBody, boolean isRequest) {
        if(plainBody == null) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new StringReader(plainBody))) {
            char[] buffer = new char[1024];
            int charsRead;

            while ((charsRead = reader.read(buffer)) != -1) {
                String content = new String(buffer, 0, charsRead);

                if(isRequest) {
                    infoRequest(correlationId, "Body-Part:%s%s", System.lineSeparator(), content);
                } else {
                    infoResponse(correlationId, "Body-Part:%s%s", System.lineSeparator(), content);
                }
            }
        } catch(IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void printJsonObject(UUID correlationId, Object body, boolean isRequest) {
        if(body == null) {
            return;
        }
 
        try {
            printPlain(correlationId, objectMapper.writeValueAsString(body), isRequest);
        } catch(IOException ex) {
            logger.warn("{}: Unable to print request body - {}", prefix, ex.getMessage());
        }
    }

    private void printHeaders(UUID correlationId, HttpHeaders headers, boolean isRequest) {
        for(var entry : headers.entrySet()) {
            String format = !hiddenHeaders.contains(entry.getKey().toLowerCase())
                ? "Header: %s=%s" : "Header: %s=[masked]";

            if(isRequest) {
                infoRequest(correlationId, format, entry.getKey(), entry.getValue());
            } else {
                infoResponse(correlationId, format, entry.getKey(), entry.getValue());
            }
        }
    }

    private void infoRequest(UUID correlationId, String format, Object... arguments) {
        String message = arguments != null ? String.format(format, arguments) : "";
        logger.info("exchange_uuid={} - {}: Request: {}", correlationId, prefix, message);
    }

    private void infoResponse(UUID correlationId, String format, Object... arguments) {
        String message = arguments != null ? String.format(format, arguments) : "";
        logger.info("exchange_uuid={} - {}: Response: {}", correlationId, prefix, message);
    }
}
