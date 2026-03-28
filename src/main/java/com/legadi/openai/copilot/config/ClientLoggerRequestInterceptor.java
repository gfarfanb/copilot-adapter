package com.legadi.openai.copilot.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class ClientLoggerRequestInterceptor implements ClientHttpRequestInterceptor {

    private final Logger logger = LoggerFactory.getLogger(ClientLoggerRequestInterceptor.class);

    private final Set<String> skipHeaders;

    public ClientLoggerRequestInterceptor(RestClientInterceptorProperties interceptorProperties) {
        this.skipHeaders = new HashSet<>(interceptorProperties.getSkipHeaders());
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, 
            ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        var response = execution.execute(request, body);
        return logResponse(request, response);
    }

    private void logRequest(HttpRequest request, byte[] body) {
        logger.info("Request: {} {}", request.getMethod(), request.getURI());
        logHeaders("Request", request.getHeaders());
        bufferedPrint("Request", new ByteArrayInputStream(body),
            (c, n) -> {});
    }

    private ClientHttpResponse logResponse(HttpRequest request, 
            ClientHttpResponse response) throws IOException {
        logger.info("Response status: {}", response.getStatusCode());
        logHeaders("Response", response.getHeaders());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        bufferedPrint("Response", response.getBody(),
            (b, r) -> outputStream.write(b.getBytes(), 0, r));

        return new BufferingClientHttpResponseWrapper(response,
            new ByteArrayInputStream(outputStream.toByteArray()));
    }

    private void logHeaders(String tag, HttpHeaders headers) {
        for(var entry : headers.entrySet()) {
            if(!skipHeaders.contains(entry.getKey().toLowerCase())) {
                logger.info("{} header: {}: {}", tag, entry.getKey(), entry.getValue());
            } else {
                logger.info("{} header: {}: [...]", tag,  entry.getKey());
            }
        }
    }

    private void bufferedPrint(String tag, InputStream inputStream, BiConsumer<String, Integer> streamConsumer) {
        logger.info("{} body:", tag);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            char[] buffer = new char[1024];
            int charsRead;

            while ((charsRead = reader.read(buffer)) != -1) {
                String content = new String(buffer, 0, charsRead);

                streamConsumer.accept(content, charsRead);

                logger.info("{}", content);
            }
        } catch(IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static class BufferingClientHttpResponseWrapper implements ClientHttpResponse {

        private final Logger logger = LoggerFactory.getLogger(BufferingClientHttpResponseWrapper.class);

        private final ClientHttpResponse response;
        private final InputStream bodyInputStream;

        public BufferingClientHttpResponseWrapper(
                ClientHttpResponse response, InputStream bodyInputStream) {
            this.response = response;
            this.bodyInputStream = bodyInputStream;
        }

        @Override
        public InputStream getBody() {
            return bodyInputStream;
        }

        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return response.getStatusCode();
        }

        @Override
        public HttpHeaders getHeaders() {
            return response.getHeaders();
        }

        @Override
        public String getStatusText() throws IOException {
            return response.getStatusText();
        }

        @Override
        public void close() {
            try {
                bodyInputStream.close();
            } catch(IOException ex) {
                logger.error("Error on closing response wrapper", ex);
            }
        }
    }
}
