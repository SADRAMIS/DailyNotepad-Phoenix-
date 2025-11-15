package com.example.dailynotes.service;

import com.example.dailynotes.entity.Task;
import com.example.dailynotes.entity.WeeklyPlan;
import com.example.dailynotes.entity.WeeklyTask;
import com.example.dailynotes.exception.EntityNotFoundException;
import com.example.dailynotes.exception.ValidationException;
import com.example.dailynotes.repository.TaskRepository;
import com.example.dailynotes.repository.WeeklyPlanRepository;
import com.example.dailynotes.repository.WeeklyTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeeklyPlanService {
    private static final Logger logger = LoggerFactory.getLogger(WeeklyPlanService.class);
    private final WeeklyPlanRepository weeklyPlanRepository;
    private final WeeklyTaskRepository weeklyTaskRepository;
    private final TaskRepository taskRepository;

    public WeeklyPlanService(WeeklyPlanRepository planRepo,WeeklyTaskRepository taskRepo,TaskRepository baseTaskRepo){
        this.weeklyPlanRepository = planRepo;
        this.weeklyTaskRepository = taskRepo;
        this.taskRepository = baseTaskRepo;
    }

    @Transactional
    public WeeklyPlan createWeeklyPlan(int weekYear, int weekNumber, List<Long> taskIds){
        if (weekNumber < 1 || weekNumber > 53) {
            throw new ValidationException("Номер недели должен быть от 1 до 53");
        }
        if (taskIds == null || taskIds.isEmpty()) {
            throw new ValidationException("Необходимо выбрать хотя бы одну задачу");
        }
        
        logger.info("Создание недельного плана: год={}, неделя={}, задач={}", weekYear, weekNumber, taskIds.size());
        
        WeeklyPlan weeklyPlan = new WeeklyPlan();
        weeklyPlan.setWeekYear(weekYear);
        weeklyPlan.setWeekNumber(weekNumber);
        weeklyPlanRepository.save(weeklyPlan);

        for(Long id : taskIds){
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Задача", id));
            WeeklyTask weeklyTask = new WeeklyTask();
            weeklyTask.setTask(task);
            weeklyTask.setWeeklyPlan(weeklyPlan);
            weeklyTask.setStatusPerDay(new HashMap<>());
            weeklyTaskRepository.save(weeklyTask);
        }

        weeklyPlan.setTasks(weeklyTaskRepository.findByWeeklyPlanId(weeklyPlan.getId()));
        WeeklyPlan saved = weeklyPlanRepository.save(weeklyPlan);
        logger.info("Недельный план создан с ID: {}", saved.getId());
        return saved;
    }
    @Transactional(readOnly = true)
    public List<WeeklyPlan> getWeekPlans(int weekYear,int weekNumber){
        return weeklyPlanRepository.findByWeekYearAndWeekNumber(weekYear, weekNumber);
    }

    @Transactional(readOnly = true)
    public List<WeeklyTask> getWeeklyTasks(Long planId){
        return weeklyTaskRepository.findByWeeklyPlanId(planId);
    }

    @Transactional
    public void updateTaskDayStatus(Long weeklyTaskId,Integer dayOfWeek,Boolean status){
        if (dayOfWeek == null || dayOfWeek < 1 || dayOfWeek > 7) {
            throw new ValidationException("День недели должен быть от 1 до 7");
        }
        if (status == null) {
            throw new ValidationException("Статус не может быть null");
        }
        
        logger.debug("Обновление статуса задачи недели: weeklyTaskId={}, day={}, status={}", weeklyTaskId, dayOfWeek, status);
        WeeklyTask weeklyTask = weeklyTaskRepository.findById(weeklyTaskId)
                .orElseThrow(() -> new EntityNotFoundException("Задача недели", weeklyTaskId));
        Map<Integer,Boolean> map = weeklyTask.getStatusPerDay();
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(dayOfWeek,status);
        weeklyTask.setStatusPerDay(map);
        weeklyTaskRepository.save(weeklyTask);
        logger.debug("Статус задачи недели обновлен");
    }
}
