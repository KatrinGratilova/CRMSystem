package org.example.crmsystem.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.PrintStream;
import java.util.Scanner;

@Configuration
@ComponentScan(basePackages = "org.example.crmsystem")
@PropertySource("classpath:application.properties")
public class StorageConfiguration {
    @Value("${file.trainee.path}")
    private String traineeFilePath;

    @Value("${file.trainer.path}")
    private String trainerFilePath;

    @Value("${file.training.path}")
    private String trainingFilePath;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
