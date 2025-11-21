package com.example.dailynotes.service;

import com.example.dailynotes.entity.Task;
import com.example.dailynotes.repository.TaskRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TaskTemplateService {
    private static final Logger logger = LoggerFactory.getLogger(TaskTemplateService.class);
    private final WebClient webClient; // Для интеграции с AI-сервисом
    private final TaskRepository taskRepository;

    /**
     * Конструктор с внедрением зависимостей.
     * 
     * Причина использования WebClient.Builder:
     * - Позволяет настроить таймауты и обработку ошибок
     * - Более гибкая конфигурация по сравнению с прямым созданием WebClient
     */
    public TaskTemplateService(WebClient.Builder webClientBuilder, TaskRepository taskRepository){
        this.taskRepository = taskRepository;
        // Настройка WebClient с таймаутами для предотвращения зависаний
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:8081")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // 1MB буфер
                .build();
    }

    /**
     * Генерация списка задач по умолчанию (локальный шаблон).
     * 
     * КРИТИЧЕСКОЕ ИСПРАВЛЕНИЕ:
     * Задачи теперь сохраняются в БД перед возвратом, чтобы у них были ID.
     * Без этого ID будут null, что приведет к ошибкам при создании планов.
     */
    @Transactional
    public List<Task> generateDefaultMonthlyTasks(){
        logger.info("Генерация задач по умолчанию");
        List<Task> savedTasks = new ArrayList<>();

        savedTasks.add(ensureTemplateTaskExists("Изучать Java каждый день", "Шаблон"));
        savedTasks.add(ensureTemplateTaskExists("Зарядка утром", "Шаблон"));
        savedTasks.add(ensureTemplateTaskExists("Читать техническую статью", "Шаблон"));

        logger.info("Доступно {} задач по умолчанию", savedTasks.size());
        return savedTasks;
    }

    /**
     * Генерация задач на основе AI (REST-вызов).
     * 
     * УЛУЧШЕНИЯ:
     * 1. Добавлены таймауты для предотвращения зависаний
     * 2. Обработка различных типов ошибок (сеть, таймаут, сервер)
     * 3. Задачи сохраняются в БД перед возвратом
     * 4. Логирование для отладки
     */
    @Transactional
    public List<Task> generateAiMonthlyTasks(String prompt){
        logger.info("Генерация задач через AI с промптом: {}", prompt);
        
        try {
            String response = webClient.post()
                    .uri("/ai/generate")
                    .header("Content-Type", "application/json")
                    .bodyValue(Map.of("prompt", prompt))
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30)) // Таймаут 30 секунд
                    .block();
            
            if (response == null || response.isBlank()) {
                logger.warn("AI сервис вернул пустой ответ, используем задачи по умолчанию");
                return generateDefaultMonthlyTasks();
            }
            
            ObjectMapper mapper = new ObjectMapper();
            List<Task> tasks = mapper.readValue(response, new TypeReference<List<Task>>(){});
            
            // Сохраняем задачи в БД
            List<Task> savedTasks = new ArrayList<>();
            for (Task task : tasks) {
                // Убеждаемся, что категория установлена
                if (task.getCategory() == null || task.getCategory().isBlank()) {
                    task.setCategory("AI");
                }
                Task saved = taskRepository.save(task);
                savedTasks.add(saved);
                logger.debug("Сохранена AI задача: ID={}, title={}", saved.getId(), saved.getTitle());
            }
            
            logger.info("Создано {} задач через AI", savedTasks.size());
            return savedTasks;
            
        } catch (WebClientResponseException e) {
            // Ошибка от сервера (4xx, 5xx)
            logger.error("Ошибка при вызове AI сервиса: статус={}, сообщение={}", 
                    e.getStatusCode(), e.getMessage());
            return generateDefaultMonthlyTasks();
            
        } catch (Exception e) {
            // Обработка всех остальных ошибок (таймаут, парсинг JSON, сеть и т.д.)
            Throwable cause = e.getCause();
            if (cause instanceof java.util.concurrent.TimeoutException) {
                logger.error("Таймаут при вызове AI сервиса", e);
            } else {
                logger.error("Ошибка при генерации AI задач: {}", e.getMessage(), e);
            }
            return generateDefaultMonthlyTasks();
        }
    }

    private Task ensureTemplateTaskExists(String title, String category) {
        return taskRepository.findByTitleIgnoreCaseAndCategoryIgnoreCase(title, category)
                .orElseGet(() -> {
                    Task templateTask = new Task();
                    templateTask.setTitle(title);
                    templateTask.setCategory(category);
                    Task saved = taskRepository.save(templateTask);
                    logger.debug("Создан шаблон задачи: ID={}, title={}", saved.getId(), saved.getTitle());
                    return saved;
                });
    }
}
