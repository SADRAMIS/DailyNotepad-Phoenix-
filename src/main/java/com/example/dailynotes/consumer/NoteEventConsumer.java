package com.example.dailynotes.consumer;

import com.example.dailynotes.event.NoteEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Consumer для обработки событий из Kafka
 * 
 * Этот класс слушает топик Kafka и обрабатывает события, связанные с заметками.
 * 
 * Как это работает:
 * 1. Kafka отправляет сообщения в топик "note-events"
 * 2. @KafkaListener автоматически получает эти сообщения
 * 3. Методы обрабатывают события в зависимости от их типа
 *
 */
@Component
public class NoteEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NoteEventConsumer.class);

    /**
     * Обработчик всех событий о заметках
     * 
     * @KafkaListener - аннотация, которая указывает Spring, что этот метод должен
     *                 слушать указанный топик Kafka
     * 
     * Параметры:
     * - topics: название топика Kafka для прослушивания
     * - groupId: ID группы потребителей (из application.properties)
     * 
     * @param event событие из Kafka (автоматически десериализуется из JSON)
     * @param partition номер партиции, из которой пришло сообщение
     * @param offset позиция сообщения в партиции
     * @param acknowledgment подтверждение обработки (для ручного управления)
     */
    @KafkaListener(
            topics = "${spring.kafka.topic.name:note-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeNoteEvent(
            @Payload NoteEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            logger.info("Получено событие из Kafka: тип={}, noteId={}, partition={}, offset={}", 
                    event.getEventType(), event.getNoteId(), partition, offset);
            
            // Обработка события в зависимости от его типа
            switch (event.getEventType()) {
                case CREATED:
                    handleNoteCreated(event);
                    break;
                case UPDATED:
                    handleNoteUpdated(event);
                    break;
                case DELETED:
                    handleNoteDeleted(event);
                    break;
                case TOGGLED:
                    handleNoteToggled(event);
                    break;
                default:
                    logger.warn("Неизвестный тип события: {}", event.getEventType());
            }
            
            // Подтверждение успешной обработки сообщения
            // Это важно для надежности - Kafka не будет повторно отправлять обработанные сообщения
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
            
            logger.info("Событие успешно обработано: noteId={}", event.getNoteId());
            
        } catch (Exception e) {
            logger.error("Ошибка при обработке события из Kafka: noteId={}, тип={}", 
                    event.getNoteId(), event.getEventType(), e);
        }
    }

    /**
     * Обработка события создания заметки
     *
     */
    private void handleNoteCreated(NoteEvent event) {
        logger.info("Обработка события CREATED для заметки ID={}, заголовок={}", 
                event.getNoteId(), event.getTitle());
        
        // Пример: можно отправить уведомление
        // notificationService.sendNotification("Заметка создана: " + event.getTitle());
        
        // Пример: можно обновить поисковый индекс
        // searchIndexService.indexNote(event);
    }

    /**
     * Обработка события обновления заметки
     */
    private void handleNoteUpdated(NoteEvent event) {
        logger.info("Обработка события UPDATED для заметки ID={}, заголовок={}", 
                event.getNoteId(), event.getTitle());
        
        // Пример: обновление кэша
        // cacheService.invalidateNote(event.getNoteId());
    }

    /**
     * Обработка события удаления заметки
     */
    private void handleNoteDeleted(NoteEvent event) {
        logger.info("Обработка события DELETED для заметки ID={}", event.getNoteId());
        
        // Пример: удаление из поискового индекса
        // searchIndexService.removeNote(event.getNoteId());
        
        // Пример: очистка кэша
        // cacheService.removeNote(event.getNoteId());
    }

    /**
     * Обработка события изменения статуса заметки
     */
    private void handleNoteToggled(NoteEvent event) {
        logger.info("Обработка события TOGGLED для заметки ID={}", event.getNoteId());
        
        // Пример: обновление статистики выполненных задач
        // statisticsService.updateCompletedTasksCount();
    }
}

