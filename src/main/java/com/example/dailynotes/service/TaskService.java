package com.example.dailynotes.service;

import com.example.dailynotes.entity.Task;
import com.example.dailynotes.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository repository){
        this.taskRepository = repository;
    }

    @Transactional
    public Task createTask(String title,String category){
        Task task = new Task();
        task.setTitle(title);
        task.setCategory(category);
        return taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    // Получить шаблонные задачи для автозаполнения
  public List<Task> getTemplateTasksForMonth(){
        return taskRepository.findAll()
                .stream().filter(t->t.getCategory().equals("Шаблон") || t.getCategory().contains("..."))
                        .toList();
    }

    // Вернуть список id для автозаполнения
    @Transactional(readOnly = true)
    public List<Long> getTemplateTaskIdsForMonth() {
        return getTemplateTasksForMonth()
                .stream().map(Task::getId).toList();
    }
}
