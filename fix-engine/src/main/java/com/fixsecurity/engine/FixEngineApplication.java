package com.fixsecurity.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * FIX Protocol Engine - Spring Boot application entry point.
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.fixsecurity")
@EntityScan("com.fixsecurity.analytics")
@EnableJpaRepositories("com.fixsecurity.analytics")
public class FixEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(FixEngineApplication.class, args);
    }
}
