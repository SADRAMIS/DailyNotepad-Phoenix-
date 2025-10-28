package com.example.dailynotes.entity;

import jakarta.persistence.*;

@Entity
public class DayTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private boolean completed;
    @ManyToOne
    private Note note;
}
