package com.example.dailynotes.repository;

import com.example.dailynotes.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
    Optional<Task> findByTitleIgnoreCaseAndCategoryIgnoreCase(String title, String category);
}
