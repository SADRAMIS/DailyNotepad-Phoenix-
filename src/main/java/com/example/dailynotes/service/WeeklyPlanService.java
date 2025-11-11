package com.example.dailynotes.service;

import com.example.dailynotes.entity.Task;
import com.example.dailynotes.entity.WeeklyPlan;
import com.example.dailynotes.entity.WeeklyTask;
import com.example.dailynotes.repository.TaskRepository;
import com.example.dailynotes.repository.WeeklyPlanRepository;
import com.example.dailynotes.repository.WeeklyTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeeklyPlanService {
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
        WeeklyPlan weeklyPlan = new WeeklyPlan();
        weeklyPlan.setWeekYear(weekYear);
        weeklyPlan.setWeekNumber(weekNumber);
        weeklyPlanRepository.save(weeklyPlan);

        for(Long id : taskIds){
            Task task = taskRepository.findById(id)
                    .orElseThrow(()->new RuntimeException("Задача не найдена"));
            WeeklyTask weeklyTask = new WeeklyTask();
            weeklyTask.setTask(task);
            weeklyTask.setWeeklyPlan(weeklyPlan);
            weeklyTask.setStatusPerDay(new HashMap<>());
            weeklyTaskRepository.save(weeklyTask);
        }

        weeklyPlan.setTasks(weeklyTaskRepository.findByWeeklyPlanId(weeklyPlan.getId()));
        return weeklyPlanRepository.save(weeklyPlan);
    }
    @Transactional(readOnly = true)
    public List<WeeklyPlan> getWeekPlans(int weekYear,int weekNumber){
        return weeklyPlanRepository.findByWeekYearAndWeekNumber(weekYear, weekNumber);
    }

    @Transactional(readOnly = true)
    public List<WeeklyTask> getWeeklyTasks(Long planId){
        return weeklyTaskRepository.findByWeeklyPlanId(planId);
    }

    public void updateTaskDayStatus(Long weeklyTaskId,Integer dayOfWeek,Boolean status){
        WeeklyTask weeklyTask = weeklyTaskRepository.findById(weeklyTaskId)
                .orElseThrow(()->new RuntimeException("Задача недели не найдена"));
        Map<Integer,Boolean> map = weeklyTask.getStatusPerDay();
        map.put(dayOfWeek,status);
        weeklyTask.setStatusPerDay(map);
        weeklyTaskRepository.save(weeklyTask);
    }
}
