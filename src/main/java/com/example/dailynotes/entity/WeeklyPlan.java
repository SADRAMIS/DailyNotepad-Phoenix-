package com.example.dailynotes.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class WeeklyPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int weekNumber;
    private int year;
    @OneToMany(mappedBy = "weeklyPlan",cascade = CascadeType.ALL)
    private List<WeeklyTask> tasks;
}
