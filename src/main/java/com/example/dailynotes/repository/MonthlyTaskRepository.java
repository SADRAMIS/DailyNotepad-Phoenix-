package com.example.dailynotes.repository;

import com.example.dailynotes.entity.MonthlyTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonthlyTaskRepository extends JpaRepository<MonthlyTask,Long> {
    List<MonthlyTask> findByMonthlyPlanId(Long planId);
}
