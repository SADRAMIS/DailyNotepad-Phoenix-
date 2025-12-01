package com.example.dailynotes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Конфигурация для работы с OpenAI API
 * 
 * Этот класс настраивает WebClient для HTTP запросов к OpenAI API.
 * WebClient - это реактивный HTTP клиент из Spring WebFlux.
 */
@Configuration
public class OpenAIConfig {

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.api.url:https://api.openai.com/v1}")
    private String apiUrl;

    /**
     * Создает WebClient для работы с OpenAI API
     * 
     * WebClient настроен с:
     * - Базовым URL OpenAI API
     * - Заголовком авторизации с API ключом
     * - Таймаутами для запросов
     */
    @Bean
    public WebClient openaiWebClient() {
        return WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * Проверяет, настроен ли API ключ
     */
    public boolean isApiKeyConfigured() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }
}

