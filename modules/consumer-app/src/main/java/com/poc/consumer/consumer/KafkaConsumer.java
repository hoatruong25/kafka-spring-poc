package com.poc.consumer.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    @KafkaListener(topics = "user-topic", groupId = "test_consumer")
    public void consume(String message) {
        System.out.println(message);
    }
}
