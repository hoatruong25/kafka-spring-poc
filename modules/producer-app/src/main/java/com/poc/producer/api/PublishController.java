package com.poc.producer.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class PublishController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.topics.user-topic}")
    private String userTopic;

    @GetMapping("/health")
    public String health() {
        return "Producer service is running!";
    }

    @PostMapping("/publish/{message}")
    public String publish(@PathVariable("message") final String message) {
        log.info("Received request to publish message: {}", message);
        
        try {
            // Send message asynchronously with callback handling
            //TODO: Research how CompletableFuture works
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(userTopic, message);
            
            future.whenComplete((result, failure) -> {
                if (failure != null) {
                    log.error("Failed to send message: {} to topic: {}", message, userTopic, failure);
                } else {
                    log.info("Successfully sent message: {} to topic: {} at offset: {}", 
                            message, userTopic, result.getRecordMetadata().offset());
                }
            });
            
            log.info("Message queued for publishing: {}", message);
            return "Published order: " + message;
            
        } catch (Exception e) {
            log.error("Exception occurred while publishing message: {}", message, e);
            throw new RuntimeException("Failed to publish message", e);
        }
    }
}
