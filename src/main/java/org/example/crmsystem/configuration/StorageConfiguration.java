package org.example.crmsystem.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.crmsystem.model.Trainee;
import org.example.crmsystem.model.Trainer;
import org.example.crmsystem.model.Training;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;

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

    @Bean
    public HashMap<Long, Trainee> traineeStorage(){
        return new HashMap<Long, Trainee>();
    }

    @Bean
    public HashMap<Long, Trainer> trainerStorage(){
        return new HashMap<Long, Trainer>();
    }

    @Bean
    public HashMap<Long, Training> trainingStorage(){
        return new HashMap<Long, Training>();
    }

    @Bean
    public String s(){
        return "src/main/resources/trainee.json";
    }
}
