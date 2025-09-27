package com.poc.consumerapp2.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.common.model.UserDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private final ObjectMapper objectMapper;

    public KafkaConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Method 1: Using ConsumerRecord to get all metadata including offset
     */
    @KafkaListener(topics = "test-topic", groupId = "test_consumer")
    public void consume(ConsumerRecord<String, String> record) {
        String message = record.value();
        long offset = record.offset();
        int partition = record.partition();
        String topic = record.topic();
        long timestamp = record.timestamp();

        try {
            // Try to parse as UserDto JSON
            if (message.startsWith("{")) {
                UserDto user = objectMapper.readValue(message, UserDto.class);
                System.out.printf("📨 [Topic: %s, Partition: %d, Offset: %d, Timestamp: %d] Received User: %s%n",
                        topic, partition, offset, timestamp, user.toString());
            } else {
                // Handle plain text messages for backward compatibility
                System.out.printf("📨 [Topic: %s, Partition: %d, Offset: %d, Timestamp: %d] Received message: %s%n",
                        topic, partition, offset, timestamp, message);
            }
        } catch (JsonProcessingException e) {
            System.err.printf("❌ [Topic: %s, Partition: %d, Offset: %d] Error parsing message as JSON: %s%n",
                    topic, partition, offset, e.getMessage());
            System.out.printf("📨 [Topic: %s, Partition: %d, Offset: %d] Received raw message: %s%n",
                    topic, partition, offset, message);
        }
    }

    /**
     * Method 2: Alternative approach using @Header annotations to get specific metadata
     */
    // @KafkaListener(topics = "test-topic-alternative", groupId = "test_consumer_alt")
    public void consumeWithHeaders(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp) {

        System.out.printf("📨 [Headers Method] Topic: %s, Partition: %d, Offset: %d, Timestamp: %d%n",
                topic, partition, offset, timestamp);
        System.out.printf("📨 Message: %s%n", message);
    }
}