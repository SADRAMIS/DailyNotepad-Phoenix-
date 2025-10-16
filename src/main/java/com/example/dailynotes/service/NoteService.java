package com.example.dailynotes.service;

import com.example.dailynotes.entity.Note;
import com.example.dailynotes.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class NoteService {

    private  NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note createNote(String title, String content,double weight, LocalDate date){
        Note note = new Note(title,content,weight,date);
        return noteRepository.save(note);
    }

    public List<Note> findNotesByDate(LocalDate date){
        return noteRepository.findByDate(date);
    }

    public void toggleNoteCompletion(Long id){
        Note note = noteRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Note not found"));
        //note.setCompleted
    }
}
