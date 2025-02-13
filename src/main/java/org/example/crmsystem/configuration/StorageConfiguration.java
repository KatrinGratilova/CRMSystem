package org.example.crmsystem.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.plugins.Singleton;
import org.example.crmsystem.entity.*;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

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

    @Bean
    @Singleton
    public SessionFactory sessionFactory() {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/gym");
        configuration.setProperty("hibernate.connection.username", "postgres");
        configuration.setProperty("hibernate.connection.password", "230218");

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        configuration.setProperty("show_sql", true);
        configuration.setProperty("hibernate.hbm2ddl.auto", "drop-and-create");

        configuration.addAnnotatedClass(TrainingEntity.class);
        configuration.addAnnotatedClass(TrainerEntity.class);
        configuration.addAnnotatedClass(TraineeEntity.class);
        configuration.addAnnotatedClass(TrainingTypeEntity.class);
        configuration.addAnnotatedClass(UserEntity.class);

        return configuration.buildSessionFactory();
    }
}
