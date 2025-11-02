package com.example.dailynotes.service;

import com.example.dailynotes.entity.MonthlyPlan;
import com.example.dailynotes.entity.MonthlyTask;
import com.example.dailynotes.entity.Task;
import com.example.dailynotes.repository.MonthlyPlanRepository;
import com.example.dailynotes.repository.MonthlyTaskRepository;
import com.example.dailynotes.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MonthlyPlanService {
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
    public MonthlyPlan createMonthlyPlan(int year, int month, List<Long> taskIds){
        MonthlyPlan plan = new MonthlyPlan();
        plan.setYear(year);
        plan.setMonth(month);
        monthlyPlanRepository.save(plan);
        // Добавляем задачи
        for(Long id : taskIds){
            Task task = taskRepository.findById(id).
                    orElseThrow(()-> new RuntimeException("Задача не найдена"));
            MonthlyTask monthlyTask = new MonthlyTask();
            monthlyTask.setTask(task);
            monthlyTask.setMonthlyPlan(plan);
            monthlyTask.setStatusPerDay(new HashMap<>());
            monthlyTaskRepository.save(monthlyTask);
        }
        // Подгружаем обновлённый список задач
        plan.setTasks(monthlyTaskRepository.findByMonthlyPlanId(plan.getId()));
        return monthlyPlanRepository.save(plan);
    }

    // Получить план на месяц
    @Transactional(readOnly = true)
    public List<MonthlyPlan> getMonthlyPlans(int year, int month){
        return monthlyPlanRepository.findByYearAndMonth(year, month);
    }

    // Получить задачи плана
    @Transactional(readOnly = true)
    public List<MonthlyTask> getMonthlyTasks(Long planId){
        return monthlyTaskRepository.findByMonthlyPlanId(planId);
    }

    // Изменить статус задачи на день
    @Transactional(readOnly = true)
    public void updateTaskDayStatus(Long monthlyTaskId,Integer day,Boolean status){
        MonthlyTask monthlyTask = monthlyTaskRepository.findById(monthlyTaskId).
                orElseThrow(()->new RuntimeException("Задача месяца не найдена"));
        Map<Integer,Boolean> map = monthlyTask.getStatusPerDay();
        map.put(day,status);
        monthlyTask.setStatusPerDay(map);
        monthlyTaskRepository.save(monthlyTask);
    }
}
