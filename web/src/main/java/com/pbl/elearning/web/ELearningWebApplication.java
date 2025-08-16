package com.pbl.elearning.web;

import com.pbl.elearning.security.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@ConfigurationPropertiesScan(basePackages = "com.pbl.elearning")
@ComponentScan(basePackages = {"com.pbl.elearning.*"})
@EntityScan(basePackages = {"com.pbl.elearning.*"})
@EnableJpaRepositories(basePackages = {"com.pbl.elearning.*"})
@EnableScheduling
public class ELearningWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(ELearningWebApplication.class, args);
    }
}