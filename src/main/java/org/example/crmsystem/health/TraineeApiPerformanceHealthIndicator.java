package org.example.crmsystem.health;

import lombok.RequiredArgsConstructor;
import org.example.crmsystem.service.AuthenticationService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class TraineeApiPerformanceHealthIndicator implements HealthIndicator {
    private final RestTemplate restTemplate;
    private final AuthenticationService authenticationService;

    @Override
    public Health health() {
        long start = System.currentTimeMillis();
        try {
            authenticationService.authenticate("trainee.HealthUsername", "testPassword");
            String apiUrl = "http://localhost:8080/trainees/trainee.HealthUsername";

            restTemplate.getForObject(apiUrl, String.class);
            long duration = System.currentTimeMillis() - start;

            if (duration > 1000) {
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