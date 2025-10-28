package com.example.dailynotes.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class MonthlyPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int month; //1-12
    private int year;

    @OneToMany(mappedBy = "monthlyPlan", cascade = CascadeType.ALL)
    private List<MonthlyTask> tasks;
}
