package org.example.crmsystem.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI gymCrmApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gym CRM API")
                        .description("API for managing gym trainees, trainers, and trainings")
                        .version("1.0.0"));
    }
}

