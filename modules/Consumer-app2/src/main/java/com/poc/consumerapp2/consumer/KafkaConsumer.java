package com.poc.consumerapp2.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.common.model.UserDto;
import com.poc.common.persistence.model.MessageError;
import com.poc.common.persistence.model.MessageReceived;
import com.poc.common.persistence.repository.MessageErrorRepository;
import com.poc.common.persistence.repository.MessageReceivedRepository;
import com.poc.consumerapp2.dto.Employee;
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

import org.springframework.kafka.support.Acknowledgment;

@Component
@lombok.extern.slf4j.Slf4j
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final MessageReceivedRepository messageReceivedRepository;
    private final MessageErrorRepository messageErrorRepository;

    public KafkaConsumer(ObjectMapper objectMapper, MessageReceivedRepository messageReceivedRepository,
            MessageErrorRepository messageErrorRepository) {
        this.objectMapper = objectMapper;
        this.messageReceivedRepository = messageReceivedRepository;
        this.messageErrorRepository = messageErrorRepository;
    }

    // #region Handle topic with header and DLQ
    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 1000, multiplier = 2), dltStrategy = DltStrategy.FAIL_ON_ERROR, autoCreateTopics = "true", include = {
            RetriableException.class, RuntimeException.class })
    @KafkaListener(topics = "topic-with-header")
    public void consume(@Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment ack) {

        if (key.equals("ErrorMessage")) {
            throw new RuntimeException("TestErrorMessage");
        }

        InsertMessageToDb(topic, partition, offset, message);

        try {
            // Try to parse as UserDto JSON
            if (message.startsWith("{")) {
                UserDto user = objectMapper.readValue(message, UserDto.class);
                log.info("📨 [Topic: {}, Partition: {}, Offset: {}, Timestamp: {}] Received User: {}",
                        topic, partition, offset, timestamp, user);
            } else {
                // Handle plain text messages for backward compatibility
                log.info("📨 [Topic: {}, Partition: {}, Offset: {}, Timestamp: {}] Received message: {}",
                        topic, partition, offset, timestamp, message);
            }
        } catch (JsonProcessingException e) {
            log.error("❌ [Topic: {}, Partition: {}, Offset: {}] Error parsing message as JSON: {}",
                    topic, partition, offset, e.getMessage());
            log.info("📨 [Topic: {}, Partition: {}, Offset: {}] Received raw message: {}",
                    topic, partition, offset, message);
        } finally {
            ack.acknowledge();
        }
    }

    @DltHandler
    public void dltHandler(@Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp) {
        log.info("📨 [Topic: {}, Partition: {}, Offset: {}, Timestamp: {}] Received raw message: {}",
                topic, partition, offset, timestamp, message);

        var messageError = new MessageError();
        messageError.setTopic(topic);
        messageError.setPartition(partition);
        messageError.setOffsetNumber(offset);
        messageError.setContent(message);
        messageError.setConsumerName("Consumer-2");

        messageErrorRepository.save(messageError);
    }
    // #endregion

    // #region Handle topic without header
    @KafkaListener(topics = "topic-without-header")
    public void consumeWithoutHeader(@Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp,
            Acknowledgment ack) {

        InsertMessageToDb(topic, partition, offset, message);

        try {
            // Try to parse as UserDto JSON
            if (message.startsWith("{")) {
                UserDto user = objectMapper.readValue(message, UserDto.class);
                log.info("📨 [Topic: {}, Partition: {}, Offset: {}, Timestamp: {}] Received User: {}",
                        topic, partition, offset, timestamp, user);
            } else {
                // Handle plain text messages for backward compatibility
                log.info("📨 [Topic: {}, Partition: {}, Offset: {}, Timestamp: {}] Received message: {}",
                        topic, partition, offset, timestamp, message);
            }
        } catch (JsonProcessingException e) {
            log.error("❌ [Topic: {}, Partition: {}, Offset: {}] Error parsing message as JSON: {}",
                    topic, partition, offset, e.getMessage());
            log.info("📨 [Topic: {}, Partition: {}, Offset: {}] Received raw message: {}",
                    topic, partition, offset, message);
        } finally {
            ack.acknowledge();
        }
    }

    // #endregion

    @KafkaListener(topics = "consume-employee", containerFactory = "avroKafkaListenerContainerFactory")
    public void consumeEmployee(@Payload Employee employee,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp,
            Acknowledgment ack) {
        InsertMessageToDb(topic, partition, offset, employee.toString());

        log.info("📨 [Topic: {}, Partition: {}, Offset: {}, Timestamp: {}] Received Employee: {}",
                topic, partition, offset, timestamp, employee);

        ack.acknowledge();
    }

    private void InsertMessageToDb(String topic, Integer partition, Long offset, String message) {
        var messageReceived = new MessageReceived();
        messageReceived.setTopic(topic);
        messageReceived.setPartition(partition);
        messageReceived.setOffsetNumber(offset);
        messageReceived.setContent(message);
        messageReceived.setConsumerName("Consumer-2");

        messageReceivedRepository.save(messageReceived);
    }
}