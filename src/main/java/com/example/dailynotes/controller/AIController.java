package com.example.dailynotes.controller;

import com.example.dailynotes.dto.AIRequest;
import com.example.dailynotes.dto.AIResponse;
import com.example.dailynotes.service.AIService;
import com.example.dailynotes.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API контроллер для AI функций
 * 
 * Предоставляет endpoints для:
 * - Генерации идей для заметок
 * - Автодополнения текста
 * - Анализа и резюме заметок
 * - Генерации заголовков
 * - Улучшения текста
 * - Генерации задач
 */
@RestController
@RequestMapping("/api/ai")
public class AIController {

    private static final Logger logger = LoggerFactory.getLogger(AIController.class);

    private final AIService aiService;
    private final NoteService noteService;

    @Autowired
    public AIController(AIService aiService, NoteService noteService) {
        this.aiService = aiService;
        this.noteService = noteService;
    }

    /**
     * Генерация идей для заметок
     * POST /api/ai/ideas
     * Body: { "prompt": "тема заметки" }
     */
    @PostMapping("/ideas")
    public ResponseEntity<AIResponse> generateIdeas(@RequestBody AIRequest request) {
        logger.info("Запрос на генерацию идей: {}", request.getPrompt());
        AIResponse response = aiService.generateIdeas(request.getPrompt());
        return ResponseEntity.ok(response);
    }

    /**
     * Автодополнение текста
     * POST /api/ai/autocomplete
     * Body: { "prompt": "начальный текст" }
     */
    @PostMapping("/autocomplete")
    public ResponseEntity<AIResponse> autocomplete(@RequestBody AIRequest request) {
        logger.info("Запрос на автодополнение текста");
        AIResponse response = aiService.autocompleteText(request.getPrompt());
        return ResponseEntity.ok(response);
    }

    /**
     * Резюме заметки
     * POST /api/ai/summarize
     * Body: { "prompt": "содержание заметки" }
     */
    @PostMapping("/summarize")
    public ResponseEntity<AIResponse> summarize(@RequestBody AIRequest request) {
        logger.info("Запрос на создание резюме заметки");
        AIResponse response = aiService.summarizeNote(request.getPrompt());
        return ResponseEntity.ok(response);
    }

    /**
     * Генерация заголовка
     * POST /api/ai/title
     * Body: { "prompt": "содержание заметки" }
     */
    @PostMapping("/title")
    public ResponseEntity<AIResponse> generateTitle(@RequestBody AIRequest request) {
        logger.info("Запрос на генерацию заголовка");
        AIResponse response = aiService.generateTitle(request.getPrompt());
        return ResponseEntity.ok(response);
    }

    /**
     * Улучшение текста
     * POST /api/ai/improve
     * Body: { "prompt": "текст для улучшения" }
     */
    @PostMapping("/improve")
    public ResponseEntity<AIResponse> improveText(@RequestBody AIRequest request) {
        logger.info("Запрос на улучшение текста");
        AIResponse response = aiService.improveText(request.getPrompt());
        return ResponseEntity.ok(response);
    }

    /**
     * Генерация задач на основе заметки
     * POST /api/ai/tasks
     * Body: { "prompt": "содержание заметки" }
     */
    @PostMapping("/tasks")
    public ResponseEntity<AIResponse> generateTasks(@RequestBody AIRequest request) {
        logger.info("Запрос на генерацию задач");
        AIResponse response = aiService.generateTasks(request.getPrompt());
        return ResponseEntity.ok(response);
    }

    /**
     * Резюме заметки по ID
     * GET /api/ai/notes/{id}/summarize
     */
    @GetMapping("/notes/{id}/summarize")
    public ResponseEntity<AIResponse> summarizeNoteById(@PathVariable Long id) {
        logger.info("Запрос на резюме заметки с ID: {}", id);
        try {
            var note = noteService.findNoteById(id);
            String content = note.getTitle() + "\n\n" + note.getContent();
            AIResponse response = aiService.summarizeNote(content);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Ошибка при получении заметки для резюме", e);
            return ResponseEntity.ok(AIResponse.error("Заметка не найдена"));
        }
    }

    /**
     * Генерация заголовка для заметки по ID
     * GET /api/ai/notes/{id}/title
     */
    @GetMapping("/notes/{id}/title")
    public ResponseEntity<AIResponse> generateTitleForNote(@PathVariable Long id) {
        logger.info("Запрос на генерацию заголовка для заметки с ID: {}", id);
        try {
            var note = noteService.findNoteById(id);
            AIResponse response = aiService.generateTitle(note.getContent());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Ошибка при получении заметки для генерации заголовка", e);
            return ResponseEntity.ok(AIResponse.error("Заметка не найдена"));
        }
    }
}

