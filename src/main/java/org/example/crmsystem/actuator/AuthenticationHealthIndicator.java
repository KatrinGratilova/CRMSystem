package org.example.crmsystem.actuator;

import lombok.RequiredArgsConstructor;
import org.example.crmsystem.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile({"local", "prod"})
public class AuthenticationHealthIndicator implements HealthIndicator {
    private final AuthenticationService authenticationService;

    @Value("${api.trainee.health.username}")
    private String healthUsername;

    @Value("${api.trainee.health.password}")
    private String healthPassword;

    @Override
    public Health health() {
        try {
            boolean isAuthenticated = authenticationService.authenticate(healthUsername, healthPassword);

            if (isAuthenticated) {
                return Health.up().withDetail("Authentication", "Success for test user").build();
            } else {
                return Health.down().withDetail("Authentication", "Failed for test user").build();
            }
        } catch (Exception e) {
            return Health.down(e).withDetail("Authentication", "Error during authentication check").build();
        }
    }
}