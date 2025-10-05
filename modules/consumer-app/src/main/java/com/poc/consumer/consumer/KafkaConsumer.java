package com.poc.consumer.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.common.model.UserDto;
import com.poc.common.persistence.model.MessageError;
import com.poc.common.persistence.model.MessageReceived;
import com.poc.common.persistence.repository.MessageErrorRepository;
import com.poc.common.persistence.repository.MessageReceivedRepository;
import org.apache.kafka.common.errors.RetriableException;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final MessageReceivedRepository messageReceivedRepository;
    private final MessageErrorRepository messageErrorRepository;

    public KafkaConsumer(ObjectMapper objectMapper, MessageReceivedRepository messageReceivedRepository, MessageErrorRepository messageErrorRepository) {
        this.objectMapper = objectMapper;
        this.messageReceivedRepository = messageReceivedRepository;
        this.messageErrorRepository = messageErrorRepository;
    }

    //#region Handle topic with header and DLQ
    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2),
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            autoCreateTopics = "true",
            include = { RetriableException.class, RuntimeException.class }
    )
    @KafkaListener(topics = "topic-with-header")
    public void consume(@Payload String message,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                        @Header(KafkaHeaders.OFFSET) long offset,
                        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp,
                        @Header(KafkaHeaders.RECEIVED_KEY) String key) {


        if (key.equals("ErrorMessage")) {
            throw new RuntimeException("TestErrorMessage");
        }

        InsertMessageToDb(topic, partition, offset, message);

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

    @DltHandler
    public void dltHandler(@Payload String message,
                           @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                           @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                           @Header(KafkaHeaders.OFFSET) long offset,
                           @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp) {
        System.out.printf("📨 [Topic: %s, Partition: %d, Offset: %d, Timestamp: %d] Received raw message: %s%n",
                topic, partition, offset, timestamp, message);

        var messageError = new MessageError();
        messageError.setTopic(topic);
        messageError.setPartition(partition);
        messageError.setOffsetNumber(offset);
        messageError.setContent(message);
        messageError.setConsumerName("Consumer-1");

        messageErrorRepository.save(messageError);
    }
    //#endregion

    //#region Handle topic without header
    @KafkaListener(topics = "topic-without-header")
    public void consumeWithoutHeader(@Payload String message,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                     @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                     @Header(KafkaHeaders.OFFSET) long offset,
                                     @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp) {

        InsertMessageToDb(topic, partition, offset, message);

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

    //#endregion

    private void InsertMessageToDb(String topic, Integer partition, Long offset, String message) {
        var messageReceived = new MessageReceived();
        messageReceived.setTopic(topic);
        messageReceived.setPartition(partition);
        messageReceived.setOffsetNumber(offset);
        messageReceived.setContent(message);
        messageReceived.setConsumerName("Consumer-1");

        messageReceivedRepository.save(messageReceived);
    }
}