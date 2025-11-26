package com.example.dailynotes.service;

import com.example.dailynotes.event.NoteEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Сервис для отправки событий в Kafka
 * 
 * Этот сервис отвечает за публикацию событий в Kafka топик.
 * Используется для асинхронной обработки событий, связанных с заметками.
 *
 */
@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    // KafkaTemplate - основной класс для отправки сообщений
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Название топика Kafka, куда отправляются события
    @Value("${spring.kafka.topic.name:note-events}")
    private String topicName;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Отправка события о создании заметки
     * 
     * @param event событие с информацией о заметке
     */
    public void sendNoteCreatedEvent(NoteEvent event) {
        sendEvent("CREATED", event);
    }

    /**
     * Отправка события об обновлении заметки
     * 
     * @param event событие с информацией о заметке
     */
    public void sendNoteUpdatedEvent(NoteEvent event) {
        sendEvent("UPDATED", event);
    }

    /**
     * Отправка события об удалении заметки
     * 
     * @param event событие с информацией о заметке
     */
    public void sendNoteDeletedEvent(NoteEvent event) {
        sendEvent("DELETED", event);
    }

    /**
     * Отправка события об изменении статуса заметки
     * 
     * @param event событие с информацией о заметке
     */
    public void sendNoteToggledEvent(NoteEvent event) {
        sendEvent("TOGGLED", event);
    }

    /**
     * Универсальный метод для отправки события в Kafka
     * 
     * @param key ключ сообщения (используется для партиционирования)
     * @param event событие для отправки
     */
    private void sendEvent(String key, NoteEvent event) {
        try {
            logger.info("Отправка события в Kafka: тип={}, noteId={}, топик={}", 
                    event.getEventType(), event.getNoteId(), topicName);

            // Отправка сообщения асинхронно
            // CompletableFuture позволяет обработать результат отправки
            CompletableFuture<SendResult<String, Object>> future = 
                    kafkaTemplate.send(topicName, key, event);

            // Обработка успешной отправки
            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    logger.info("Событие успешно отправлено в Kafka: offset={}, partition={}", 
                            result.getRecordMetadata().offset(),
                            result.getRecordMetadata().partition());
                } else {
                    logger.error("Ошибка при отправке события в Kafka: {}", exception.getMessage(), exception);
                }
            });

        } catch (Exception e) {
            logger.error("Критическая ошибка при отправке события в Kafka", e);
            // В реальном приложении здесь можно добавить fallback механизм
            // Например, сохранить событие в базу данных для последующей отправки
        }
    }
}



