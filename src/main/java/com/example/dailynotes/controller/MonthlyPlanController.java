package com.example.dailynotes.controller;

import com.example.dailynotes.entity.MonthlyPlan;
import com.example.dailynotes.entity.Task;
import com.example.dailynotes.service.MonthlyPlanService;
import com.example.dailynotes.service.TaskService;
import com.example.dailynotes.service.TaskTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/plans/month")
public class MonthlyPlanController {
    private static final Logger logger = LoggerFactory.getLogger(MonthlyPlanController.class);
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
    public String showMonthlyPlan(@RequestParam(required = false) Integer planYear,
                                  @RequestParam(required = false) Integer planMonth,
                                  Model model){
        LocalDate today = LocalDate.now();
        int resolvedYear = planYear != null ? planYear : today.getYear();
        int resolvedMonth = planMonth != null ? planMonth : today.getMonthValue();

        if (resolvedMonth < 1 || resolvedMonth > 12) {
            model.addAttribute("error", "Месяц должен быть от 1 до 12");
            resolvedMonth = today.getMonthValue();
        }

        model.addAttribute("planYear", resolvedYear);
        model.addAttribute("planMonth", resolvedMonth);

        List<Task> allTasks = taskService.getAllTasks();
        model.addAttribute("allTasks", allTasks);
        model.addAttribute("hasAvailableTasks", !allTasks.isEmpty());

        try {
            List<MonthlyPlan> plans = monthlyPlanService.getMonthlyPlans(resolvedYear,resolvedMonth);
            model.addAttribute("plans",plans);
            int daysInMonth = LocalDate.of(resolvedYear,resolvedMonth,1).lengthOfMonth();
            model.addAttribute("days", IntStream.rangeClosed(1,daysInMonth).boxed().toList());
        } catch (Exception e) {
            logger.error("Ошибка при получении месячного плана: год={}, месяц={}", resolvedYear, resolvedMonth, e);
            model.addAttribute("error", "Ошибка при загрузке плана: " + e.getMessage());
            model.addAttribute("plans", List.of());
            model.addAttribute("days", List.of());
        }
        return "monthlyPlan";
    }

    // Форма создания нового плана с выбором задач
    @GetMapping("/new")
    public String newPlanForm(){
        LocalDate today = LocalDate.now();
        return "redirect:/plans/month?planYear=" + today.getYear() + "&planMonth=" + today.getMonthValue();
    }

    // Создать план с выбранными задачами
    @PostMapping
    public String createMonthlyPlan(@RequestParam int planYear, @RequestParam int planMonth,
                                     @RequestParam(required = false) List<Long> taskIds,
                                     RedirectAttributes redirectAttributes){
        try {
            if (taskIds == null || taskIds.isEmpty()) {
                if (!taskService.getAllTasks().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Выберите хотя бы одну задачу для плана");
                    return "redirect:/plans/month?planYear=" + planYear + "&planMonth=" + planMonth;
                }
                List<Task> generated = taskTemplateService.generateDefaultMonthlyTasks();
                taskIds = generated.stream().map(Task::getId).toList();
                redirectAttributes.addFlashAttribute("info", "Список задач был создан автоматически");
            }

            monthlyPlanService.createMonthlyPlan(planYear, planMonth, taskIds);
            redirectAttributes.addFlashAttribute("success", "Месячный план успешно создан");
        } catch (Exception e) {
            logger.error("Ошибка при создании месячного плана: год={}, месяц={}", planYear, planMonth, e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании плана: " + e.getMessage());
        }
        return "redirect:/plans/month?planYear=" + planYear + "&planMonth=" + planMonth;
    }

    // Автозаполнение через AI/шаблон
    @PostMapping("/autofill")
    public String autofillMonthlyTasks(@RequestParam int planYear, @RequestParam int planMonth, 
                                       @RequestParam(required = false) String prompt,
                                       RedirectAttributes redirectAttributes){
        try {
            List<Task> tasks;
            if(prompt != null && !prompt.isBlank()){
                tasks = taskTemplateService.generateAiMonthlyTasks(prompt);
            }else{
                tasks = taskTemplateService.generateDefaultMonthlyTasks();
            }
            List<Long> taskIds = tasks.stream().map(Task::getId).toList();
            monthlyPlanService.createMonthlyPlan(planYear,planMonth,taskIds);
            redirectAttributes.addFlashAttribute("success", "План успешно создан с автозаполнением");
        } catch (Exception e) {
            logger.error("Ошибка при автозаполнении плана: год={}, месяц={}", planYear, planMonth, e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при автозаполнении: " + e.getMessage());
        }
        return "redirect:/plans/month?planYear=" + planYear + "&planMonth=" + planMonth;
    }

    // Отметить выполненность задачи на день
    @PostMapping("/task-status")
    public String updateMonthlyTaskStatus(@RequestParam Long monthlyTaskId,
                                         @RequestParam Integer dayNumber,
                                         @RequestParam Boolean status,
                                         @RequestParam int planYear,
                                         @RequestParam int planMonth,
                                         RedirectAttributes redirectAttributes){
        try {
            monthlyPlanService.updateTaskDayStatus(monthlyTaskId,dayNumber,status);
            redirectAttributes.addFlashAttribute("success", "Статус задачи обновлен");
        } catch (Exception e) {
            logger.error("Ошибка при обновлении статуса задачи: taskId={}, day={}", monthlyTaskId, dayNumber, e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении статуса: " + e.getMessage());
        }
        return "redirect:/plans/month?planYear=" + planYear + "&planMonth=" + planMonth;
    }
}
