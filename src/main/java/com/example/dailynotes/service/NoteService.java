package com.example.dailynotes.service;

import com.example.dailynotes.entity.Note;
import com.example.dailynotes.exception.EntityNotFoundException;
import com.example.dailynotes.repository.NoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
public class NoteService {

    private static final Logger logger = LoggerFactory.getLogger(NoteService.class);
    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Transactional
    public Note createNote(String title, String content,double weight, LocalDate date){
        logger.debug("Создание заметки: title={}, date={}", title, date);
        Note note = new Note(title,content,date,weight);
        Note saved = noteRepository.save(note);
        logger.info("Заметка создана с ID: {}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Note> findNotesByDate(LocalDate date){
        return noteRepository.findByDate(date);
    }

    @Transactional(readOnly = true)
    public Note findNoteById(Long id){
        logger.debug("Поиск заметки по ID: {}", id);
        return noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Заметка", id));
    }

    @Transactional
    public Note updateNote(Long id, Note updatedNote){
        logger.debug("Обновление заметки с ID: {}", id);
        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Заметка", id));

        existingNote.setTitle(updatedNote.getTitle());
        existingNote.setContent(updatedNote.getContent());
        existingNote.setWeight(updatedNote.getWeight());
        existingNote.setDate(updatedNote.getDate());

        Note saved = noteRepository.save(existingNote);
        logger.info("Заметка с ID {} обновлена", id);
        return saved;
    }

    @Transactional
    public void toggleNoteCompletion(Long id){
        logger.debug("Переключение статуса заметки с ID: {}", id);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Заметка", id));
        note.setCompleted(!note.isCompleted());
        noteRepository.save(note);
        logger.info("Статус заметки с ID {} изменен на: {}", id, note.isCompleted());
    }

    @Transactional
    public void deleteNote(Long id){
        logger.debug("Удаление заметки с ID: {}", id);
        if(!noteRepository.existsById(id)){
            throw new EntityNotFoundException("Заметка", id);
        }
        noteRepository.deleteById(id);
        logger.info("Заметка с ID {} удалена", id);
    }
}
