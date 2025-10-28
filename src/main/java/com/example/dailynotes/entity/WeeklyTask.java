package com.example.dailynotes.entity;

import jakarta.persistence.*;

import java.util.Map;

@Entity
public class WeeklyTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Task task;
    @ManyToOne
    private WeeklyPlan weeklyPlan;
    // храним статусы завершения для каждого дня недели (1-пн,7-вс)
    @ElementCollection
    private Map<Integer,Boolean> statusPerDay;
}
