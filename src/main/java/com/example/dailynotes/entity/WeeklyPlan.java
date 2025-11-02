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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<WeeklyTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<WeeklyTask> tasks) {
        this.tasks = tasks;
    }
}
