package com.example.dailynotes.service;

import com.example.dailynotes.entity.Note;
import com.example.dailynotes.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    public void createNote(String title, String content){
        Note note = new Note(title,content);
        noteRepository.save(note);
    }
}
