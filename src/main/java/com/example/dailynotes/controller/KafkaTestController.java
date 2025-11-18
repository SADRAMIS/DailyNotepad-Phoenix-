package com.example.dailynotes.controller;

import com.example.dailynotes.service.KafkaProducerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("kafka")
public class KafkaTestController {
    private final KafkaProducerService kafkaProducerService;

    public KafkaTestController(KafkaProducerService kafkaProducerService){
        this.kafkaProducerService = kafkaProducerService;
    }
    @PostMapping("/send")
    public String sendMessage(@RequestParam String message){
        kafkaProducerService.sendMessage("training_topic",message);
        return "Отправлено: " + message;
    }
}
