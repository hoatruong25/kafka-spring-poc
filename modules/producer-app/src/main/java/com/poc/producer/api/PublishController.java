package com.poc.producer.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.common.model.UserDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/")
@Tag(name = "Kafka Producer API", description = "APIs for publishing messages to Kafka topics")
public class PublishController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private final String topic = "test-topic";
    
    // Fake data arrays for generating realistic user data
    private final String[] names = {"John Doe", "Jane Smith", "Mike Johnson", "Sarah Wilson", "David Brown", 
                                   "Lisa Davis", "Tom Miller", "Emily Taylor", "Chris Anderson", "Anna Garcia"};
    private final String[] departments = {"Engineering", "Marketing", "Sales", "HR", "Finance", 
                                        "Operations", "IT", "Legal", "Research", "Support"};
    private final Random random = new Random();

    public PublishController(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/publish/{message}")
    public String publish(@PathVariable("message") final String message) {

        kafkaTemplate.send(topic, message);

        return "Published order: " + message;
    }

    @PostMapping("/bulk-message/{count}")
    public String publishBulkMessages(@PathVariable("count") final int count) {
        List<UserDto> publishedUsers = new ArrayList<>();
        
        try {
            for (int i = 1; i <= count; i++) {
                UserDto user = generateFakeUser(i);
                String userJson = objectMapper.writeValueAsString(user);
                
                // Gửi WITHOUT key (round-robin distribution)
                kafkaTemplate.send(topic, userJson);
                publishedUsers.add(user);
            }
            
            return String.format("Successfully published %d user messages to Kafka topic '%s' (round-robin distribution)", count, topic);
            
        } catch (JsonProcessingException e) {
            return "Error publishing messages: " + e.getMessage();
        }
    }
    
    @PostMapping("/bulk-message-with-key/{count}")
    public String publishBulkMessagesWithKey(@PathVariable("count") final int count) {
        List<UserDto> publishedUsers = new ArrayList<>();
        
        try {
            for (int i = 1; i <= count; i++) {
                UserDto user = generateFakeUser(i);
                String userJson = objectMapper.writeValueAsString(user);
                
                // Gửi WITH key (hash-based distribution)
                String key = user.getDept(); // Dùng department làm key
                kafkaTemplate.send(topic, key, userJson);
                publishedUsers.add(user);
            }
            
            return String.format("Successfully published %d user messages to Kafka topic '%s' (key-based distribution by department)", count, topic);
            
        } catch (JsonProcessingException e) {
            return "Error publishing messages: " + e.getMessage();
        }
    }
    
    @PostMapping("/bulk-message-specific-partition/{count}/{partition}")
    public String publishBulkMessagesToSpecificPartition(
            @PathVariable("count") final int count,
            @PathVariable("partition") final int partition) {
        
        if (partition < 0 || partition > 2) {
            return "Error: Partition must be 0, 1, or 2 (we have 3 partitions)";
        }
        
        List<UserDto> publishedUsers = new ArrayList<>();
        
        try {
            for (int i = 1; i <= count; i++) {
                UserDto user = generateFakeUser(i);
                String userJson = objectMapper.writeValueAsString(user);
                
                // Gửi đến partition cụ thể
                kafkaTemplate.send(topic, partition, null, userJson);
                publishedUsers.add(user);
            }
            
            return String.format("Successfully published %d user messages to partition %d of topic '%s'", count, partition, topic);
            
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
}