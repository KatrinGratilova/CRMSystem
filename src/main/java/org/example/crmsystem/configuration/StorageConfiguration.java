package org.example.crmsystem.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "org.example.crmsystem")
@PropertySource("classpath:application.properties")
public class StorageConfiguration {
    @Value("${file.trainee.path}")
    public String traineeFilePath;

    @Value("${file.trainer.path}")
    public String trainerFilePath;

    @Value("${file.training.path}")
    public String trainingFilePath;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

}
