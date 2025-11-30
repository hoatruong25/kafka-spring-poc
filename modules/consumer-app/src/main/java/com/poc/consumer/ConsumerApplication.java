package com.poc.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = { "com.poc.common.persistence.model" })
@EnableJpaRepositories(basePackages = { "com.poc.common.persistence.repository" })
@lombok.extern.slf4j.Slf4j
public class ConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
        log.info("🚀 Consumer Application 1 started successfully!");
    }
}
