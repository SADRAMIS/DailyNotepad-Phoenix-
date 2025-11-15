package com.example.dailynotes.exception;

/**
 * Исключение, выбрасываемое когда сущность не найдена в базе данных.
 * Это более информативная альтернатива RuntimeException для случаев,
 * когда запрашиваемая сущность отсутствует.
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entityType, Long id) {
        super(String.format("%s с ID %d не найдена", entityType, id));
    }
}

