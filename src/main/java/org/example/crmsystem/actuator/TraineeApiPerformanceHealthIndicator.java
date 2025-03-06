package org.example.crmsystem.actuator;

import lombok.RequiredArgsConstructor;
import org.example.crmsystem.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Profile({"local", "prod"})
public class TraineeApiPerformanceHealthIndicator implements HealthIndicator {
    private final RestTemplate restTemplate;
    private final AuthenticationService authenticationService;

    @Value("${api.trainee.url}")
    private String traineeApiUrl;

    @Value("${api.trainee.health.username}")
    private String healthUsername;

    @Value("${api.trainee.health.password}")
    private String healthPassword;

    @Value("${api.trainee.threshold}")
    private int responseTimeThreshold;

    @Override
    public Health health() {
        long start = System.currentTimeMillis();
        try {
            authenticationService.authenticate(healthUsername, healthPassword);
            String apiUrl = traineeApiUrl + healthUsername;

            restTemplate.getForObject(apiUrl, String.class);
            long duration = System.currentTimeMillis() - start;

            if (duration > responseTimeThreshold) {
                return Health.down()
                        .withDetail("API response time", duration + "ms")
                        .withDetail("error", "Response time is too high")
                        .build();
            }
            return Health.up().withDetail("API response time", duration + "ms").build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}