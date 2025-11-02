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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public WeeklyPlan getWeeklyPlan() {
        return weeklyPlan;
    }

    public void setWeeklyPlan(WeeklyPlan weeklyPlan) {
        this.weeklyPlan = weeklyPlan;
    }

    public Map<Integer, Boolean> getStatusPerDay() {
        return statusPerDay;
    }

    public void setStatusPerDay(Map<Integer, Boolean> statusPerDay) {
        this.statusPerDay = statusPerDay;
    }
}
