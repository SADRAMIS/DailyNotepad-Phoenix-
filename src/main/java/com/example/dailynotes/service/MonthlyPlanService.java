package com.example.dailynotes.service;

import com.example.dailynotes.entity.MonthlyPlan;
import com.example.dailynotes.entity.MonthlyTask;
import com.example.dailynotes.entity.Task;
import com.example.dailynotes.exception.EntityNotFoundException;
import com.example.dailynotes.exception.ValidationException;
import com.example.dailynotes.repository.MonthlyPlanRepository;
import com.example.dailynotes.repository.MonthlyTaskRepository;
import com.example.dailynotes.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MonthlyPlanService {
    private static final Logger logger = LoggerFactory.getLogger(MonthlyPlanService.class);
    private final MonthlyPlanRepository monthlyPlanRepository;
    private final MonthlyTaskRepository monthlyTaskRepository;
    private final TaskRepository taskRepository;

    public MonthlyPlanService(MonthlyPlanRepository planRepo,MonthlyTaskRepository taskRepo,TaskRepository baseTaskRepo){
        this.monthlyPlanRepository = planRepo;
        this.monthlyTaskRepository = taskRepo;
        this.taskRepository = baseTaskRepo;
    }

    // Создать план на месяц с выбранными задачами
    @Transactional
    public MonthlyPlan createMonthlyPlan(int planYear, int planMonth, List<Long> taskIds){
        // Валидация входных данных
        if (planMonth < 1 || planMonth > 12) {
            throw new ValidationException("Месяц должен быть от 1 до 12");
        }
        if (planYear < 2000 || planYear > 2100) {
            throw new ValidationException("Год должен быть от 2000 до 2100");
        }
        if (taskIds == null || taskIds.isEmpty()) {
            throw new ValidationException("Необходимо выбрать хотя бы одну задачу");
        }

        logger.info("Создание месячного плана: год={}, месяц={}, задач={}", planYear, planMonth, taskIds.size());
        
        MonthlyPlan plan = new MonthlyPlan();
        plan.setPlanYear(planYear);
        plan.setPlanMonth(planMonth);
        monthlyPlanRepository.save(plan);
        
        // Добавляем задачи
        for(Long id : taskIds){
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Задача", id));
            MonthlyTask monthlyTask = new MonthlyTask();
            monthlyTask.setTask(task);
            monthlyTask.setMonthlyPlan(plan);
            monthlyTask.setStatusPerDay(new HashMap<>());
            monthlyTaskRepository.save(monthlyTask);
        }
        // Подгружаем обновлённый список задач
        plan.setTasks(monthlyTaskRepository.findByMonthlyPlanId(plan.getId()));
        MonthlyPlan saved = monthlyPlanRepository.save(plan);
        logger.info("Месячный план создан с ID: {}", saved.getId());
        return saved;
    }

    // Получить план на месяц
    @Transactional(readOnly = true)
    public List<MonthlyPlan> getMonthlyPlans(int planYear, int planMonth){
        return monthlyPlanRepository.findByPlanYearAndPlanMonth(planYear, planMonth);
    }

    // Получить задачи плана
    @Transactional(readOnly = true)
    public List<MonthlyTask> getMonthlyTasks(Long planId){
        return monthlyTaskRepository.findByMonthlyPlanId(planId);
    }

    // Изменить статус задачи на день
    @Transactional
    public void updateTaskDayStatus(Long monthlyTaskId,Integer day,Boolean status){
        if (day == null || day < 1 || day > 31) {
            throw new ValidationException("День должен быть от 1 до 31");
        }
        if (status == null) {
            throw new ValidationException("Статус не может быть null");
        }
        
        logger.debug("Обновление статуса задачи: monthlyTaskId={}, day={}, status={}", monthlyTaskId, day, status);
        MonthlyTask monthlyTask = monthlyTaskRepository.findById(monthlyTaskId)
                .orElseThrow(() -> new EntityNotFoundException("Задача месяца", monthlyTaskId));
        Map<Integer,Boolean> map = monthlyTask.getStatusPerDay();
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(day,status);
        monthlyTask.setStatusPerDay(map);
        monthlyTaskRepository.save(monthlyTask);
        logger.debug("Статус задачи обновлен");
    }
}
