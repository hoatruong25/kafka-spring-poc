package com.poc.consumerapp2;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.poc.common.persistence.model"})
@EnableJpaRepositories(basePackages = {"com.poc.common.persistence.repository"})
public class Consumer2Application {
    public static void main(String[] args) {
        SpringApplication.run(Consumer2Application.class, args);
    }
}
