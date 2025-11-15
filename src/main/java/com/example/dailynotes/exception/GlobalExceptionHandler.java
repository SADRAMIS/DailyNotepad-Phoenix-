package com.example.dailynotes.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeParseException;

/**
 * Глобальный обработчик исключений для всего приложения.
 * 
 * Преимущества:
 * - Централизованная обработка ошибок
 * - Единообразные сообщения об ошибках для пользователя
 * - Логирование всех исключений для отладки
 * - Предотвращение показа технических деталей пользователю
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Обработка случаев, когда сущность не найдена.
     * Возвращает пользователю понятное сообщение об ошибке.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFound(EntityNotFoundException ex, Model model) {
        logger.warn("Сущность не найдена: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("errorTitle", "Запись не найдена");
        return "error";
    }

    /**
     * Обработка ошибок валидации.
     * Показывает пользователю, что именно неверно в его вводе.
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationException(ValidationException ex, RedirectAttributes redirectAttributes) {
        logger.warn("Ошибка валидации: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/";
    }

    /**
     * Обработка ошибок парсинга даты.
     * Важно для NoteController, где пользователь может ввести неверную дату.
     */
    @ExceptionHandler(DateTimeParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleDateTimeParseException(DateTimeParseException ex, RedirectAttributes redirectAttributes) {
        logger.warn("Ошибка парсинга даты: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", 
            "Неверный формат даты. Используйте формат YYYY-MM-DD (например, 2024-01-15)");
        return "redirect:/";
    }

    /**
     * Обработка всех остальных исключений.
     * Логирует полную информацию для разработчика, но показывает
     * пользователю только общее сообщение об ошибке.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        logger.error("Неожиданная ошибка: ", ex);
        model.addAttribute("error", "Произошла внутренняя ошибка сервера. Пожалуйста, попробуйте позже.");
        model.addAttribute("errorTitle", "Ошибка сервера");
        return "error";
    }
}

