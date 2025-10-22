package com.example.dailynotes.controller;

import com.example.dailynotes.entity.Note;
import com.example.dailynotes.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        noteService.createNote(note.getTitle(),note.getContent(),note.getWeight(),note.getDate());
        return "redirect:/notes";
    }

    @GetMapping("/")
    public String showNoteBook(@RequestParam(required = false) String date, Model model){
        LocalDate currentDate = (date != null)
                ? LocalDate.parse(date)
                : LocalDate.now();

        model.addAttribute("date", currentDate);
        model.addAttribute("note",new Note());
        model.addAttribute("todayNotes",noteService.findNotesByDate(LocalDate.now()));
        model.addAttribute("weight",75.0);

        return "notebook";
    }

    @PostMapping("/notes/{id}/toggle")
    public String toggleNote(@PathVariable Long id){
        noteService.toggleNoteCompletion(id);
        return "redirect:/";
    }

    @PostMapping("/notes/{id}/delete")
    public String deleteNote(@PathVariable Long id){
        noteService.deleteNote(id);
        return "redirect/";
    }

    @GetMapping("/notes/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model){
        Note note = noteService.findNoteById(id);
        model.addAttribute("note",note);
        return "editForm";
    }

    @PostMapping("/notes/{id}/update")
    public String updateNote(@PathVariable Long id, Model model){
        noteService.updateNote(id,note);
        return "redirect:/";
    }

}
