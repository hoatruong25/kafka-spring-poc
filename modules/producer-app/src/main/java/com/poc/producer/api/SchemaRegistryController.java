package com.poc.producer.api;

import com.poc.consumerapp2.dto.Employee;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/schema-registry/")
@Tag(name = "Schema Registry API", description = "")
public class SchemaRegistryController {
    private final KafkaTemplate<String, Employee> kafkaTemplate;

    private final String topic = "consume-employee";

    private final String[] names = {"John Doe", "Jane Smith", "Mike Johnson", "Sarah Wilson", "David Brown",
            "Lisa Davis", "Tom Miller", "Emily Taylor", "Chris Anderson", "Anna Garcia"};
    private final String[] departments = {"Engineering", "Marketing", "Sales", "HR", "Finance",
            "Operations", "IT", "Legal", "Research", "Support"};
    private final Random random = new Random();

    public SchemaRegistryController(KafkaTemplate<String, Employee> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/publish")
    public String publishEmployee() {
        var employee = generateFakeEmployee(0);
        kafkaTemplate.send(topic, employee);

        return "Oke";
    }

    private Employee generateFakeEmployee(int index) {
        String name = names[random.nextInt(names.length)] + " #" + index;
        String department = departments[random.nextInt(departments.length)];
        Long salary = 30000L + random.nextInt(70000); // Salary between 30k-100k

        return new Employee(name, department, salary);
    }
}
