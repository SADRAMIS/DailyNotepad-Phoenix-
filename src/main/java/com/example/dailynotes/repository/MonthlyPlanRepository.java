package com.example.dailynotes.repository;

import com.example.dailynotes.entity.MonthlyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonthlyPlanRepository extends JpaRepository<MonthlyPlan,Long> {
    List<MonthlyPlan> findByYearAndMonth(int year,int month);
}
