package com.example.dailynotes.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private LocalDate date;
    private double weight;
    private boolean completed;



    public Note(){}

    public Note(String title, String content,double weight){
        this.title = title;
        this.content = content;
        this.date = LocalDate.now();
        this.weight = weight;
    }

    public Note(String title, String content,LocalDate date,double weight){
        this.title = title;
        this.content = content;
        this.date = date;
        this.weight = weight;
        this.completed = false;
    }

    @OneToMany(mappedBy = "note",cascade = CascadeType.ALL)
    private List<DayTask> dayTasks;

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
