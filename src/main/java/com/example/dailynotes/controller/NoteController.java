package com.example.dailynotes.controller;

import com.example.dailynotes.entity.Note;
import com.example.dailynotes.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Controller
public class NoteController {

    private static final Logger logger = LoggerFactory.getLogger(NoteController.class);
    private final NoteService noteService;

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
    public String createNote(@ModelAttribute Note note, RedirectAttributes redirectAttributes){
        // Валидация входных данных
        if (note.getTitle() == null || note.getTitle().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Заголовок не может быть пустым");
            return "redirect:/";
        }
        if (note.getContent() == null || note.getContent().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Содержание не может быть пустым");
            return "redirect:/";
        }
        if (note.getDate() == null) {
            note.setDate(LocalDate.now());
        }
        
        try {
            noteService.createNote(
                    note.getTitle(),
                    note.getContent(),
                    note.getWeight(),
                    note.getDate());
            redirectAttributes.addFlashAttribute("success", "Заметка успешно создана");
        } catch (Exception e) {
            logger.error("Ошибка при создании заметки", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании заметки: " + e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/")
    public String showNoteBook(@RequestParam(required = false) String date, Model model){
        LocalDate currentDate;
        
        // Обработка парсинга даты с обработкой ошибок
        if (date != null && !date.trim().isEmpty()) {
            try {
                currentDate = LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                logger.warn("Неверный формат даты: {}", date);
                model.addAttribute("error", 
                    "Неверный формат даты. Используйте формат YYYY-MM-DD (например, 2024-01-15)");
                currentDate = LocalDate.now();
            }
        } else {
            currentDate = LocalDate.now();
        }

        model.addAttribute("date", currentDate);
        model.addAttribute("note",new Note());
        model.addAttribute("todayNotes",noteService.findNotesByDate(currentDate));
        model.addAttribute("weight",75.0);

        return "notebook";
    }

    @PostMapping("/notes/{id}/toggle")
    public String toggleNote(@PathVariable Long id, RedirectAttributes redirectAttributes){
        try {
            noteService.toggleNoteCompletion(id);
            redirectAttributes.addFlashAttribute("success", "Статус заметки изменен");
        } catch (Exception e) {
            logger.error("Ошибка при изменении статуса заметки с ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при изменении статуса: " + e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/notes/{id}/delete")
    public String deleteNote(@PathVariable Long id, RedirectAttributes redirectAttributes){
        try {
            noteService.deleteNote(id);
            redirectAttributes.addFlashAttribute("success", "Заметка удалена");
        } catch (Exception e) {
            logger.error("Ошибка при удалении заметки с ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении: " + e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/notes/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model){
        Note note = noteService.findNoteById(id);
        model.addAttribute("note",note);
        return "editForm";
    }

    @PostMapping("/notes/{id}/update")
    public String updateNote(
            @PathVariable Long id,
            @ModelAttribute Note note,
            RedirectAttributes redirectAttributes){
        // Валидация
        if (note.getTitle() == null || note.getTitle().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Заголовок не может быть пустым");
            return "redirect:/notes/" + id + "/edit";
        }
        if (note.getContent() == null || note.getContent().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Содержание не может быть пустым");
            return "redirect:/notes/" + id + "/edit";
        }
        
        try {
            noteService.updateNote(id, note);
            redirectAttributes.addFlashAttribute("success", "Заметка успешно обновлена");
        } catch (Exception e) {
            logger.error("Ошибка при обновлении заметки с ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении: " + e.getMessage());
            return "redirect:/notes/" + id + "/edit";
        }
        return "redirect:/";
    }
}
