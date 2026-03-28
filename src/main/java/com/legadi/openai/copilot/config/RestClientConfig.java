package com.legadi.openai.copilot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private final CopilotProperties copilotProperties;
    private final RestClientInterceptorProperties interceptorProperties;

    public RestClientConfig(CopilotProperties copilotProperties,
        RestClientInterceptorProperties interceptorProperties) {
        this.copilotProperties = copilotProperties;
        this.interceptorProperties = interceptorProperties;
    }

    @Bean
    public RestClient copilotWebClient() {
        return RestClient.builder()
            .baseUrl(copilotProperties.getApiUrl())
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + copilotProperties.getToken())
            .defaultHeader("X-GitHub-Api-Version", copilotProperties.getVersion())
            .defaultHeader("Accept", "application/vnd.github+json")
            .requestInterceptor(copilotRequestInterceptor())
            .build();
    }

    @Bean
    public ClientHttpRequestInterceptor copilotRequestInterceptor() {
        return new ClientLoggerRequestInterceptor(interceptorProperties);
    }
}
