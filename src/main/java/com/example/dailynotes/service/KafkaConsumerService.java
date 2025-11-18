package com.example.dailynotes.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    @KafkaListener(topics = "training_topic",groupId = "test-group")
    public void listen(String message){
        System.out.println("Получено из kafka" + message);
    }
}
