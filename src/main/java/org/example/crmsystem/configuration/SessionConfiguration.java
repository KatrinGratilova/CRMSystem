package org.example.crmsystem.configuration;

import org.example.crmsystem.entity.*;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = "org.example.crmsystem")
@PropertySource("classpath:application.properties")
public class SessionConfiguration {
    @Value("${spring.datasource.driver-class-name}")
    private String driverClass;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.jpa.database-platform}")
    private String dialect;

    @Value("${spring.jpa.show-sql}")
    private boolean showSql;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SessionFactory sessionFactory() {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

        configuration.setProperty("hibernate.connection.driver_class", driverClass);
        configuration.setProperty("hibernate.connection.url", url);
        configuration.setProperty("hibernate.connection.username", username);
        configuration.setProperty("hibernate.connection.password", password);
        configuration.setProperty("hibernate.dialect", dialect);
        configuration.setProperty("hibernate.show_sql", String.valueOf(showSql));
        configuration.setProperty("hibernate.hbm2ddl.auto", ddlAuto);

        configuration.addAnnotatedClass(TrainingEntity.class);
        configuration.addAnnotatedClass(TrainerEntity.class);
        configuration.addAnnotatedClass(TraineeEntity.class);
        configuration.addAnnotatedClass(TrainingTypeEntity.class);
        configuration.addAnnotatedClass(UserEntity.class);

        return configuration.buildSessionFactory();
    }
}
