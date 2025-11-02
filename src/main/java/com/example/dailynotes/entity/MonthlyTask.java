package com.example.dailynotes.entity;

import jakarta.persistence.*;

import java.util.Map;

@Entity
public class MonthlyTask {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Task task;

    @ManyToOne
    private MonthlyPlan monthlyPlan;

    //храним статусы завершения для каждого дня месяца
    @ElementCollection //позволяет хранить много значений,а не одно поле
    private Map<Integer,Boolean> statusPerDay;

    public Map<Integer, Boolean> getStatusPerDay() {
        return statusPerDay;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatusPerDay(Map<Integer, Boolean> statusPerDay) {
        this.statusPerDay = statusPerDay;
    }

    public MonthlyPlan getMonthlyPlan() {
        return monthlyPlan;
    }

    public void setMonthlyPlan(MonthlyPlan monthlyPlan) {
        this.monthlyPlan = monthlyPlan;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
