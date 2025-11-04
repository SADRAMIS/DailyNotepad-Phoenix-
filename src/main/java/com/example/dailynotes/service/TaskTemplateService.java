package com.example.dailynotes.service;

import com.example.dailynotes.entity.Task;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TaskTemplateService {
    private final WebClient webClient; // Для интеграции с AI-сервисом

    public TaskTemplateService(WebClient.Builder webClientBuilder){
        // Надо будет Указать адрес микросервиса AI или OpenAI API
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    // Список типовых задач (локальный шаблон)
    public List<Task> generateDefaultMonthlyTasks(){
        List<Task> list = new ArrayList<>();
        Task task1 = new Task();
        task1.setTitle("Изучать Java каждый день");
        task1.setCategory("Шаблон");
        list.add(task1);

        Task task2 = new Task();
        task2.setTitle("Зарядка утром");
        task2.setCategory("Шаблон");
        list.add(task2);

        Task task3 = new Task();
        task3.setTitle("Читать техническую статью");
        task3.setCategory("Шаблон");
        list.add(task3);

        return list;
    }

    // Генерация задач на основе AI (REST-вызов)
    public List<Task> generateAiMonthlyTasks(String prompt){
        String response = webClient.post()
                .uri("/ai/generate")
                .header("Content-Type", "application/json")
                .bodyValue(Map.of("prompt", prompt))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        ObjectMapper mapper = new ObjectMapper();
        try{
            return mapper.readValue(response, new TypeReference<List<Task>>(){});
            }catch (Exception e){
            return generateDefaultMonthlyTasks();
        }
    }
}
