package com.example.dailynotes;

import com.example.dailynotes.entity.Note;
import com.example.dailynotes.repository.NoteRepository;
import com.example.dailynotes.service.NoteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NoteServiceTest {
    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    @Test
    void createNote_WithValidData_ShouldSaveNoteWithCorrectAttributes(){

        String testTitle = "Тестовая заметка";
        String testContent = "Содержание тестовой заметки";
        double testWeight = 75.5;
        LocalDate testDate = LocalDate.of(2025,5,10);

        Note expectedNote = new Note(testTitle,testContent,testWeight);
        when(noteRepository.save(any(Note.class))).thenReturn(expectedNote);

        Note result = noteService.createNote(testTitle,testContent,testWeight,testDate);

        assertNotNull(result);
        assertEquals(testTitle,result.getTitle());
        assertNotNull(testContent,result.getContent());

        verify(noteRepository,times(1)).save(any(Note.class));


    }
}
