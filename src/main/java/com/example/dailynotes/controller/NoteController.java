package com.example.dailynotes.controller;

import com.example.dailynotes.entity.Note;
import com.example.dailynotes.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;

@Controller
public class NoteController {

    private NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/notes/new")
    public String showNoteForm(Model model){
        model.addAttribute("note",new Note());
        return "noteForm";
    }

    @PostMapping("/notes")
    public String createNote(@ModelAttribute Note note){
        noteService.createNote(note.getTitle(),note.getContent(),note.getWeight());
        return "redirect:/notes";
    }
    @GetMapping("/")
    public String showNoteBook(Model model){
        model.addAttribute("date", LocalDate.now());
        model.addAttribute("note",new Note());
        model.addAttribute("todayNotes",noteService.findNotesByDate(currentDate));
        model.addAttribute("weight",75.0);

        return "notebook";
    }

}
