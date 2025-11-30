package com.poc.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = { "com.poc.common.persistence.model" })
@EnableJpaRepositories(basePackages = { "com.poc.common.persistence.repository" })
@lombok.extern.slf4j.Slf4j
public class ProducerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
        log.info("🚀 Producer Application started successfully!");
    }
}
