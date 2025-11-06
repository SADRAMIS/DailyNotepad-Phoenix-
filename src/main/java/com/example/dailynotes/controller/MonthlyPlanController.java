package com.example.dailynotes.controller;

import com.example.dailynotes.entity.MonthlyPlan;
import com.example.dailynotes.entity.Task;
import com.example.dailynotes.service.MonthlyPlanService;
import com.example.dailynotes.service.TaskService;
import com.example.dailynotes.service.TaskTemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    @GetMapping("/new")
    public String newPlanForm(Model model){
        model.addAttribute("allTasks",taskService.getAllTasks());
        return "newMonthlyPlanForm";
    }

    // Создать план с выбранными задачами
    @PostMapping
    public String createMonthlyPlan(@RequestParam int year, @RequestParam int month, @RequestParam List<Long> taskIds){
        monthlyPlanService.createMonthlyPlan(year, month, taskIds);
        return "redirect:/plans/month?year=" + year + "&month=" + month;
    }

    // Автозаполнение через AI/шаблон
    @PostMapping("/autofill")
    public String autofillMonthlyTasks(@RequestParam int year, @RequestParam int month, @RequestParam(required = false) String prompt){
        List<Task> tasks;
        if(prompt != null && !prompt.isBlank()){
            tasks = taskTemplateService.generateAiMonthlyTasks(prompt);
        }else{
            tasks = taskTemplateService.generateDefaultMonthlyTasks();
        }
        List<Long> taskIds = tasks.stream().map(Task::getId).toList();
        monthlyPlanService.createMonthlyPlan(year,month,taskIds);
        return "redirect:/plans/month?year=" + year + "&month=" + month;
    }

    // Отметить выполненность задачи на день
    @PostMapping("/task-status")
    public String updateMonthlyTaskStatus(@RequestParam Long monthlyTaskId,@RequestParam Integer dayNumber,@RequestParam Boolean status,@RequestParam int year,@RequestParam int month){
        monthlyPlanService.updateTaskDayStatus(monthlyTaskId,dayNumber,status);
        return "redirect:/plans/month?year=" + year + "&month=" + month;
    }
}
