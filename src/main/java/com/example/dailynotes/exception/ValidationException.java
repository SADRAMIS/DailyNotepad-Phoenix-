package com.example.dailynotes.exception;

/**
 * Исключение для ошибок валидации входных данных.
 * Используется когда пользовательский ввод не соответствует требованиям.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}

