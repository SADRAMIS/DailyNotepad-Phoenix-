package com.example.dailynotes.service;

import com.example.dailynotes.entity.Note;
import com.example.dailynotes.event.NoteEvent;
import com.example.dailynotes.exception.EntityNotFoundException;
import com.example.dailynotes.repository.NoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
public class NoteService {

    private static final Logger logger = LoggerFactory.getLogger(NoteService.class);
    private final NoteRepository noteRepository;
    private final KafkaProducerService kafkaProducerService;
    private final NoteCacheService noteCacheService;

    /**
     * Конструктор с внедрением зависимостей
     * 
     * @param noteRepository репозиторий для работы с базой данных
     * @param kafkaProducerService сервис для отправки событий в Kafka
     */
    public NoteService(NoteRepository noteRepository,
                       KafkaProducerService kafkaProducerService,
                       NoteCacheService noteCacheService) {
        this.noteRepository = noteRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.noteCacheService = noteCacheService;
    }

    /**
     * Создание новой заметки
     * После сохранения в БД отправляет событие CREATED в Kafka
     */
    @Transactional
    public Note createNote(String title, String content,double weight, LocalDate date){
        logger.debug("Создание заметки: title={}, date={}", title, date);
        Note note = new Note(title,content,date,weight);
        Note saved = noteRepository.save(note);
        logger.info("Заметка создана с ID: {}", saved.getId());

        noteCacheService.evictNotesByDate(saved.getDate());
        noteCacheService.evictNoteById(saved.getId());
        
        // Отправка события о создании заметки в Kafka
        // Это происходит асинхронно и не блокирует выполнение метода
        try {
            NoteEvent event = new NoteEvent(NoteEvent.EventType.CREATED, saved);
            kafkaProducerService.sendNoteCreatedEvent(event);
        } catch (Exception e) {
            // Логируем ошибку, но не прерываем выполнение
            // Заметка уже сохранена в БД, событие можно отправить позже
            logger.error("Ошибка при отправке события о создании заметки в Kafka", e);
        }
        
        return saved;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = NoteCacheService.NOTES_BY_DATE_CACHE, key = "#date")
    public List<Note> findNotesByDate(LocalDate date){
        return noteRepository.findByDate(date);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = NoteCacheService.NOTES_BY_ID_CACHE, key = "#id")
    public Note findNoteById(Long id){
        logger.debug("Поиск заметки по ID: {}", id);
        return noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Заметка", id));
    }

    /**
     * Обновление существующей заметки
     * После сохранения в БД отправляет событие UPDATED в Kafka
     */
    @Transactional
    public Note updateNote(Long id, Note updatedNote){
        logger.debug("Обновление заметки с ID: {}", id);
        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Заметка", id));
        LocalDate previousDate = existingNote.getDate();

        existingNote.setTitle(updatedNote.getTitle());
        existingNote.setContent(updatedNote.getContent());
        existingNote.setWeight(updatedNote.getWeight());
        existingNote.setDate(updatedNote.getDate());

        Note saved = noteRepository.save(existingNote);
        logger.info("Заметка с ID {} обновлена", id);

        noteCacheService.evictNoteById(id);
        noteCacheService.evictNotesByDates(previousDate, saved.getDate());
        
        // Отправка события об обновлении заметки в Kafka
        try {
            NoteEvent event = new NoteEvent(NoteEvent.EventType.UPDATED, saved);
            kafkaProducerService.sendNoteUpdatedEvent(event);
        } catch (Exception e) {
            logger.error("Ошибка при отправке события об обновлении заметки в Kafka", e);
        }
        
        return saved;
    }

    /**
     * Изменение статуса выполнения заметки
     * После сохранения в БД отправляет событие TOGGLED в Kafka
     */
    @Transactional
    public void toggleNoteCompletion(Long id){
        logger.debug("Переключение статуса заметки с ID: {}", id);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Заметка", id));
        note.setCompleted(!note.isCompleted());
        Note saved = noteRepository.save(note);
        logger.info("Статус заметки с ID {} изменен на: {}", id, saved.isCompleted());

        noteCacheService.evictNoteById(id);
        noteCacheService.evictNotesByDate(saved.getDate());
        
        // Отправка события об изменении статуса заметки в Kafka
        try {
            NoteEvent event = new NoteEvent(NoteEvent.EventType.TOGGLED, saved);
            kafkaProducerService.sendNoteToggledEvent(event);
        } catch (Exception e) {
            logger.error("Ошибка при отправке события об изменении статуса заметки в Kafka", e);
        }
    }

    /**
     * Удаление заметки
     * ВАЖНО: Событие отправляется ДО удаления, так как после удаления объект Note недоступен
     */
    @Transactional
    public void deleteNote(Long id){
        logger.debug("Удаление заметки с ID: {}", id);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Заметка", id));
        LocalDate noteDate = note.getDate();
        
        // Отправка события об удалении заметки в Kafka
        // Делаем это ДО удаления, чтобы иметь доступ к данным заметки
        try {
            NoteEvent event = new NoteEvent(NoteEvent.EventType.DELETED, note);
            kafkaProducerService.sendNoteDeletedEvent(event);
        } catch (Exception e) {
            logger.error("Ошибка при отправке события об удалении заметки в Kafka", e);
        }
        
        noteRepository.deleteById(id);
        logger.info("Заметка с ID {} удалена", id);

        noteCacheService.evictNoteById(id);
        noteCacheService.evictNotesByDate(noteDate);
    }
}
