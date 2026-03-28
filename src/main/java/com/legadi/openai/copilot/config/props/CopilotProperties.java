package com.legadi.openai.copilot.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "copilot")
@Getter @Setter
public class CopilotProperties {

    private String token;
    private String apiUrl;
    private String version;
}
