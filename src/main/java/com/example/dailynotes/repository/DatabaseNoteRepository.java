package com.example.dailynotes.repository;

import com.example.dailynotes.entity.Note;
import org.springframework.stereotype.Component;

@Component
public class DatabaseNoteRepository implements NoteRepository{
    @Override
    public void save(Note note) {
        // реализация сохранения в базу данных
        System.out.println("Сохраняем заметку в базе данных: " + note.getTitle());
    }
}
