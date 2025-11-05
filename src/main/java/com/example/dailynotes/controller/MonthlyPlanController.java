package com.example.dailynotes.controller;

import com.example.dailynotes.entity.MonthlyPlan;
import com.example.dailynotes.service.MonthlyPlanService;
import com.example.dailynotes.service.TaskService;
import com.example.dailynotes.service.TaskTemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/plans/month")
public class MonthlyPlanController {
    private final MonthlyPlanService monthlyPlanService;
    private final TaskService taskService;
    private final TaskTemplateService taskTemplateService;

    public MonthlyPlanController(MonthlyPlanService planService, TaskService taskService, TaskTemplateService templateService){
        this.monthlyPlanService = planService;
        this.taskService = taskService;
        this.taskTemplateService = templateService;
    }

    // Просмотр плана месяца
    @GetMapping
    public String showMonthlyPlan(@RequestParam int year, @RequestParam int month, Model model){
        List<MonthlyPlan> plans = monthlyPlanService.getMonthlyPlans(year,month);
        model.addAttribute("plans",plans);
        int daysInMonth = LocalDate.of(year,month,1).lengthOfMonth();
        model.addAttribute("days", IntStream.rangeClosed(1,daysInMonth).boxed().toList());
        return "monthlyPlan";
    }

    // Форма создания нового плана с выбором задач
    @GetMapping
    public String newPlanForm(Model model){
        model.addAttribute("allTasks",taskService.getAllTasks());
        return "newMonthlyPlanForm";
    }
}
