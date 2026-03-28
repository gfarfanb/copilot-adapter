package com.legadi.openai.copilot.config;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class WebFilterConfig {

    @Value("${rest.service.request.hidden-headers}")
    private List<String> requestHiddenHeaders;

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        Set<String> hidden = requestHiddenHeaders.stream().map(String::toLowerCase)
            .collect(Collectors.toSet());
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(false);
        filter.setIncludeHeaders(true);
        filter.setHeaderPredicate(h -> !hidden.contains(h.toLowerCase()));
        return filter;
    }
}
