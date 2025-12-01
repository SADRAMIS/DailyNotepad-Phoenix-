package com.example.dailynotes.service;

import com.example.dailynotes.config.OpenAIConfig;
import com.example.dailynotes.dto.AIResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для работы с AI (OpenAI)
 * 
 * Предоставляет следующие возможности:
 * 1. Генерация идей для заметок
 * 2. Автодополнение текста
 * 3. Анализ и резюме заметок
 * 4. Генерация заголовков на основе содержания
 * 5. Улучшение текста заметок
 */
@Service
public class AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    private final WebClient webClient;
    private final OpenAIConfig openAIConfig;
    private final ObjectMapper objectMapper;

    @Value("${openai.model:gpt-3.5-turbo}")
    private String model;

    @Value("${openai.max-tokens:500}")
    private Integer defaultMaxTokens;

    @Autowired
    public AIService(@Qualifier("openaiWebClient") WebClient openaiWebClient, OpenAIConfig openAIConfig) {
        this.webClient = openaiWebClient;
        this.openAIConfig = openAIConfig;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Генерация идей для заметок на основе темы или контекста
     * 
     * @param topic тема для генерации идей
     * @return список идей в виде текста
     */
    public AIResponse generateIdeas(String topic) {
        if (!openAIConfig.isApiKeyConfigured()) {
            return AIResponse.error("OpenAI API ключ не настроен. Установите openai.api.key в application.properties");
        }

        String prompt = String.format(
            "Предложи 5-7 интересных идей для заметки на тему: '%s'. " +
            "Идеи должны быть практичными и вдохновляющими. " +
            "Ответь кратко, каждая идея на отдельной строке.",
            topic
        );

        return callOpenAI(prompt, defaultMaxTokens);
    }

    /**
     * Автодополнение текста заметки
     * 
     * @param partialText начальный текст заметки
     * @return дополненный текст
     */
    public AIResponse autocompleteText(String partialText) {
        if (!openAIConfig.isApiKeyConfigured()) {
            return AIResponse.error("OpenAI API ключ не настроен");
        }

        String prompt = String.format(
            "Продолжи следующую заметку естественным образом, сохраняя стиль и тон: '%s'",
            partialText
        );

        return callOpenAI(prompt, 300);
    }

    /**
     * Анализ и создание резюме заметки
     * 
     * @param noteContent содержание заметки
     * @return резюме заметки
     */
    public AIResponse summarizeNote(String noteContent) {
        if (!openAIConfig.isApiKeyConfigured()) {
            return AIResponse.error("OpenAI API ключ не настроен");
        }

        String prompt = String.format(
            "Проанализируй следующую заметку и создай краткое резюме (2-3 предложения):\n\n%s",
            noteContent
        );

        return callOpenAI(prompt, 200);
    }

    /**
     * Генерация заголовка на основе содержания заметки
     * 
     * @param noteContent содержание заметки
     * @return предложенный заголовок
     */
    public AIResponse generateTitle(String noteContent) {
        if (!openAIConfig.isApiKeyConfigured()) {
            return AIResponse.error("OpenAI API ключ не настроен");
        }

        String prompt = String.format(
            "На основе следующего текста заметки предложи краткий и информативный заголовок (максимум 10 слов):\n\n%s",
            noteContent
        );

        return callOpenAI(prompt, 50);
    }

    /**
     * Улучшение и редактирование текста заметки
     * 
     * @param noteContent исходный текст заметки
     * @return улучшенный текст
     */
    public AIResponse improveText(String noteContent) {
        if (!openAIConfig.isApiKeyConfigured()) {
            return AIResponse.error("OpenAI API ключ не настроен");
        }

        String prompt = String.format(
            "Улучши следующий текст заметки: исправь грамматические ошибки, улучши стиль, " +
            "сделай текст более читаемым, но сохрани оригинальный смысл и тон:\n\n%s",
            noteContent
        );

        return callOpenAI(prompt, defaultMaxTokens);
    }

    /**
     * Генерация задач на основе заметки
     * 
     * @param noteContent содержание заметки
     * @return список задач
     */
    public AIResponse generateTasks(String noteContent) {
        if (!openAIConfig.isApiKeyConfigured()) {
            return AIResponse.error("OpenAI API ключ не настроен");
        }

        String prompt = String.format(
            "На основе следующей заметки предложи 3-5 конкретных задач или действий. " +
            "Каждая задача должна быть на отдельной строке и начинаться с глагола:\n\n%s",
            noteContent
        );

        return callOpenAI(prompt, 300);
    }

    /**
     * Универсальный метод для вызова OpenAI API
     * 
     * @param prompt промпт для AI
     * @param maxTokens максимальное количество токенов в ответе
     * @return ответ от AI
     */
    private AIResponse callOpenAI(String prompt, Integer maxTokens) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", new Object[]{
                Map.of("role", "user", "content", prompt)
            });
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("temperature", 0.7);

            logger.debug("Отправка запроса к OpenAI: модель={}, maxTokens={}", model, maxTokens);

            String response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            if (response == null) {
                logger.error("Получен пустой ответ от OpenAI");
                return AIResponse.error("Пустой ответ от AI сервиса");
            }

            logger.debug("Получен ответ от OpenAI: {}", response);

            // Парсинг JSON ответа
            JsonNode jsonNode = objectMapper.readTree(response);
            String content = jsonNode
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            if (content == null || content.trim().isEmpty()) {
                logger.error("Не удалось извлечь контент из ответа OpenAI");
                return AIResponse.error("Не удалось получить ответ от AI");
            }

            logger.info("Успешно получен ответ от OpenAI, длина: {}", content.length());
            return new AIResponse(content.trim());

        } catch (Exception e) {
            logger.error("Ошибка при вызове OpenAI API", e);
            return AIResponse.error("Ошибка при обращении к AI: " + e.getMessage());
        }
    }
}

