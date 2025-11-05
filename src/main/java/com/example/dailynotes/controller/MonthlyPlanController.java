package com.example.dailynotes.controller;

import com.example.dailynotes.service.MonthlyPlanService;
import com.example.dailynotes.service.TaskService;
import com.example.dailynotes.service.TaskTemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
