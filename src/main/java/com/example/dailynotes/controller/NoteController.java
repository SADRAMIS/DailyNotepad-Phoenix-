package com.example.dailynotes.controller;

import com.example.dailynotes.entity.Note;
import com.example.dailynotes.service.NoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NoteController {

    private NoteService noteService;

    @GetMapping("/notes/new")
    public String showNoteForm(Model model){
        model.addAttribute("note",new Note());
        return "add-note";
    }

    @PostMapping("/notes")
    public String createNote(@ModelAttribute Note note){
        noteService.createNote(note.getTitle(),note.getContent());
        return "redirect:/notes";
    }

}
