package com.example.dailynotes.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация Kafka для Producer и Consumer
 * 
 * Этот класс настраивает:
 * 1. KafkaProducerFactory - фабрику для создания Producer'ов, которые отправляют сообщения в Kafka
 * 2. KafkaConsumerFactory - фабрику для создания Consumer'ов, которые читают сообщения из Kafka
 * 3. ConcurrentKafkaListenerContainerFactory - фабрику для создания контейнеров слушателей Kafka
 */
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    /**
     * Конфигурация Producer'а
     * Producer отправляет сообщения в Kafka топики
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        
        // Адрес Kafka брокера (сервера)
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        
        // Сериализатор для ключа сообщения (String)
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        
        // Сериализатор для значения сообщения (JSON)
        // JsonSerializer позволяет отправлять Java объекты в формате JSON
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        // Настройки надежности доставки
        configProps.put(ProducerConfig.ACKS_CONFIG, "all"); // Ждем подтверждения от всех реплик
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3); // Количество попыток при ошибке
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // Гарантия уникальности сообщений
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * KafkaTemplate - основной класс для отправки сообщений в Kafka
     * Используется в сервисах для публикации событий
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Конфигурация Consumer'а
     * Consumer читает сообщения из Kafka топиков
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        
        // Адрес Kafka брокера
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        
        // ID группы потребителей - позволяет нескольким Consumer'ам работать вместе
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        
        // Сериализатор для ключа
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        
        // Сериализатор для значения (JSON)
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        
        // Настройки десериализации JSON
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // Доверяем всем пакетам (для разработки)
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.example.dailynotes.event.NoteEvent");
        
        // Откуда начинать читать сообщения при первом запуске
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        // Автоматическое подтверждение обработки сообщений
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        
        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * Фабрика для создания контейнеров слушателей Kafka
     * Используется для создания @KafkaListener методов
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        
        // Позволяет обрабатывать несколько сообщений параллельно
        factory.setConcurrency(3);
        
        return factory;
    }
}

