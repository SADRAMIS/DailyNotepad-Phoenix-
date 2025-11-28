package com.example.dailynotes.event;

import com.example.dailynotes.entity.Note;
import java.time.LocalDateTime;

/**
 * Модель события для Kafka
 * 
 * Это класс, который представляет событие, связанное с заметкой (Note).
 * События отправляются в Kafka при создании, обновлении, удалении заметок.
 * 
 * Типы событий:
 * - CREATED: заметка создана
 * - UPDATED: заметка обновлена
 * - DELETED: заметка удалена
 * - TOGGLED: статус выполнения заметки изменен
 */
public class NoteEvent {
    
    /**
     * Тип события
     */
    public enum EventType {
        CREATED,   // Создание заметки
        UPDATED,   // Обновление заметки
        DELETED,   // Удаление заметки
        TOGGLED    // Изменение статуса выполнения
    }
    
    private EventType eventType;      // Тип события
    private Long noteId;              // ID заметки
    private String title;             // Заголовок заметки
    private String content;           // Содержание заметки
    private LocalDateTime timestamp;  // Время создания события
    private Long userId;              // ID пользователя (для будущего расширения)
    
    // Конструкторы
    public NoteEvent() {
        this.timestamp = LocalDateTime.now();
    }
    
    public NoteEvent(EventType eventType, Note note) {
        this.eventType = eventType;
        this.noteId = note.getId();
        this.title = note.getTitle();
        this.content = note.getContent();
        this.timestamp = LocalDateTime.now();
    }
    
    public NoteEvent(EventType eventType, Long noteId) {
        this.eventType = eventType;
        this.noteId = noteId;
        this.timestamp = LocalDateTime.now();
    }
    
    // Геттеры и сеттеры
    public EventType getEventType() {
        return eventType;
    }
    
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
    
    public Long getNoteId() {
        return noteId;
    }
    
    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    @Override
    public String toString() {
        return "NoteEvent{" +
                "eventType=" + eventType +
                ", noteId=" + noteId +
                ", title='" + title + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}




