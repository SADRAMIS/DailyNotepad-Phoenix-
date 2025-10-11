package com.example.dailynotes.service;

import com.example.dailynotes.entity.Note;
import com.example.dailynotes.repository.NoteRepository;
import org.springframework.stereotype.Service;


@Service
public class NoteService {

    private  NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note createNote(String title, String content,double weight){
        Note note = new Note(title,content,weight);
        return noteRepository.save(note);
    }
}
