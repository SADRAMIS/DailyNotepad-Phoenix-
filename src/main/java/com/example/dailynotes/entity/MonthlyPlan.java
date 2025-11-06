package com.example.dailynotes.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class MonthlyPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int planMonth; //1-12
    private int planYear;

    @OneToMany(mappedBy = "monthlyPlan", cascade = CascadeType.ALL)
    private List<MonthlyTask> tasks;

    public int getPlanYear() {
        return planYear;
    }

    public void setPlanYear(int planYear) {
        this.planYear = planYear;
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

    public int getPlanMonth() {
        return planMonth;
    }

    public void setPlanMonth(int planMonth) {
        this.planMonth = planMonth;
    }
}
