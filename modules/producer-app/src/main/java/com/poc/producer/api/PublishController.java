package com.poc.producer.api;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class PublishController {

    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.topics.user-topic}")
    private String userTopic;

    @Autowired
    public PublishController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @PostMapping("/publish/{message}")
    public String publish(@PathVariable("message") final String message) {

        kafkaTemplate.send(userTopic, message);

        return "Published order: " + message;
    }
}
