package com.legadi.openai.copilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CopilotAdapterApplication {

    public static void main(String[] args) {
        SpringApplication.run(CopilotAdapterApplication.class, args);
    }
}
