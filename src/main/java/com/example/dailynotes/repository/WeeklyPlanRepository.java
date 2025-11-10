package com.example.dailynotes.repository;

import com.example.dailynotes.entity.WeeklyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeeklyPlanRepository extends JpaRepository<WeeklyPlan,Long> {
    List<WeeklyPlan> findByYearAndWeekNumber(int weekYear,int weekNumber);
}
