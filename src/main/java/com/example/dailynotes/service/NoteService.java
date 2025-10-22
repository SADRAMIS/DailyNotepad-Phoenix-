package com.example.dailynotes.service;

import com.example.dailynotes.entity.Note;
import com.example.dailynotes.repository.NoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Transactional
    public Note createNote(String title, String content,double weight, LocalDate date){
        Note note = new Note(title,content,date,weight);
        return noteRepository.save(note);
    }

    @Transactional(readOnly = true)
    public List<Note> findNotesByDate(LocalDate date){
        return noteRepository.findByDate(date);
    }

    @Transactional(readOnly = true)
    public Note findNoteById(Long id){
        return noteRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Заметка с ID " + id + " не найдена"));
    }

    @Transactional
    public Note updateNote(Long id, Note updatedNote){
        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заметка с ID " + id + " не найдена"));

        existingNote.setTitle(updatedNote.getTitle());
        existingNote.setContent(updatedNote.getContent());
        existingNote.setWeight(updatedNote.getWeight());
        existingNote.setDate(updatedNote.getDate());

        return noteRepository.save(existingNote);
    }

    @Transactional
    public void toggleNoteCompletion(Long id){
        Note note = noteRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Заметка с ID " + id + " не найдена"));
        note.setCompleted(!note.isCompleted());
        noteRepository.save(note);
    }

    @Transactional
    public void deleteNote(Long id){
        if(!noteRepository.existsById(id)){
            throw new RuntimeException("Заметка с ID " + id + " не найдена");
        }
        noteRepository.deleteById(id);
    }
}
