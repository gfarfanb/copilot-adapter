package com.legadi.openai.copilot.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "rest.client.request.interceptor")
@Getter @Setter
public class RestClientInterceptorProperties {

    private List<String> skipHeaders;
}
