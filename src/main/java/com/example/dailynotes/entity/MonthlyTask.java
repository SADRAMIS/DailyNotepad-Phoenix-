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

}
