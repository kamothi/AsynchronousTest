package com.kim.websocketTest;

import com.kim.websocketTest.ExampleHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;


import java.util.Collections;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class WebSocketConfig implements WebFluxConfigurer {

    private final ExampleHandler exampleHandler;

    @Bean
    public SimpleUrlHandlerMapping simpleUrlHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(Map.of("/ws/**", exampleHandler));
        mapping.setOrder(1);
        // CORS 설정 추가
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        mapping.setCorsConfigurations(Collections.singletonMap("/ws/**", corsConfiguration));
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
