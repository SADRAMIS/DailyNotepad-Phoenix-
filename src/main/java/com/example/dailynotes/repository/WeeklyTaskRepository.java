package com.example.dailynotes.repository;

import com.example.dailynotes.entity.WeeklyTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeeklyTaskRepository extends JpaRepository<WeeklyTask,Long> {
    List<WeeklyTask> findByWeeklyPlanId(Long planId);
}
