package com.poc.producer.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.common.model.UserDto;
import com.poc.producer.request.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.poc.common.persistence.model.MessageSend;
import com.poc.common.persistence.repository.MessageSendRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/")
@Tag(name = "Kafka Producer API", description = "APIs for publishing messages to Kafka topics")
public class PublishController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final MessageSendRepository messageSendRepository;

    private final String topic = "test-topic";
    
    // Fake data arrays for generating realistic user data
    private final String[] names = {"John Doe", "Jane Smith", "Mike Johnson", "Sarah Wilson", "David Brown", 
                                   "Lisa Davis", "Tom Miller", "Emily Taylor", "Chris Anderson", "Anna Garcia"};
    private final String[] departments = {"Engineering", "Marketing", "Sales", "HR", "Finance", 
                                        "Operations", "IT", "Legal", "Research", "Support"};
    private final Random random = new Random();

    public PublishController(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, MessageSendRepository messageSendRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.messageSendRepository = messageSendRepository;
    }

    @PostMapping("/publish")
    public String publish(@RequestBody PublishMessageRequest request) {
        kafkaTemplate.send(topic, request.getMessage());

        insertMessageToDb(request.getMessage());
        return "Published order: " + request.getMessage();
    }

    @PostMapping("/bulk-message")
    public String publishBulkMessages(@RequestBody PublishBulkMessageRequest request) {
        List<UserDto> publishedUsers = new ArrayList<>();
        
        try {
            for (int i = 1; i <= request.getCount(); i++) {
                UserDto user = generateFakeUser(i);
                String userJson = objectMapper.writeValueAsString(user);
                
                // Gửi WITHOUT key (round-robin distribution)
                kafkaTemplate.send(topic, userJson);
                publishedUsers.add(user);

                insertMessageToDb(userJson);
            }
            
            return String.format("Successfully published %d user messages to Kafka topic '%s' (round-robin distribution)", request.getCount(), topic);
            
        } catch (JsonProcessingException e) {
            return "Error publishing messages: " + e.getMessage();
        }
    }
    
    @PostMapping("/bulk-message-with-key")
    public String publishBulkMessagesWithKey(@RequestBody PublishBulkMessageWithKeyRequest request) {
        List<UserDto> publishedUsers = new ArrayList<>();
        
        try {
            for (int i = 1; i <= request.getCount(); i++) {
                UserDto user = generateFakeUser(i);
                String userJson = objectMapper.writeValueAsString(user);
                
                // Gửi WITH key (hash-based distribution)
                String key = user.getDept(); // Dùng department làm key
                kafkaTemplate.send(topic, key, userJson);
                publishedUsers.add(user);

                insertMessageToDb(userJson);
            }
            
            return String.format("Successfully published %d user messages to Kafka topic '%s' (key-based distribution by department)", request.getCount(), topic);
            
        } catch (JsonProcessingException e) {
            return "Error publishing messages: " + e.getMessage();
        }
    }
    
    @PostMapping("/bulk-message-specific-partition")
    public String publishBulkMessagesToSpecificPartition(@RequestBody PublishBulkMessageSpecificPartitionRequest request) {
        
        if (request.getPartition() < 0 || request.getPartition() > 2) {
            return "Error: Partition must be 0, 1, or 2 (we have 3 partitions)";
        }
        
        List<UserDto> publishedUsers = new ArrayList<>();
        
        try {
            for (int i = 1; i <= request.getCount(); i++) {
                UserDto user = generateFakeUser(i);
                String userJson = objectMapper.writeValueAsString(user);
                
                // Gửi đến partition cụ thể
                kafkaTemplate.send(topic, request.getPartition(), null, userJson);
                publishedUsers.add(user);

                insertMessageToDb(userJson);
            }
            
            return String.format("Successfully published %d user messages to partition %d of topic '%s'", request.getCount(), request.getPartition(), topic);
            
        } catch (JsonProcessingException e) {
            return "Error publishing messages: " + e.getMessage();
        }
    }
    
    /**
     * Generates fake user data for testing purposes
     */
    private UserDto generateFakeUser(int index) {
        String name = names[random.nextInt(names.length)] + " #" + index;
        String department = departments[random.nextInt(departments.length)];
        Long salary = 30000L + random.nextInt(70000); // Salary between 30k-100k
        
        return new UserDto(name, department, salary);
    }

    private void insertMessageToDb(String content) {
        var messageSend = new MessageSend();
        messageSend.setContent(content);
        messageSendRepository.save(messageSend);
    }
}