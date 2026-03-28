package com.legadi.openai.copilot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import com.legadi.openai.copilot.config.props.CopilotProperties;

@Configuration
public class WebClientConfig {

    private final CopilotProperties copilotProperties;

    public WebClientConfig(CopilotProperties copilotProperties) {
        this.copilotProperties = copilotProperties;
    }

    @Bean
    public WebClient copilotWebClient() {
        return WebClient.builder()
            .baseUrl(copilotProperties.getApiUrl())
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + copilotProperties.getToken())
            .defaultHeader("X-GitHub-Api-Version", copilotProperties.getVersion())
            .defaultHeader("Accept", "application/vnd.github+json")
            .build();
    }
}
