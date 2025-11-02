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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<MonthlyTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<MonthlyTask> tasks) {
        this.tasks = tasks;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
