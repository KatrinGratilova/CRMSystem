package org.example.crmsystem.health;

import lombok.RequiredArgsConstructor;
import org.example.crmsystem.service.AuthenticationService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;


@Component("authentication")
@RequiredArgsConstructor
public class AuthenticationHealthIndicator implements HealthIndicator {

    private final AuthenticationService authenticationService;

    @Override
    public Health health() {
        String testUsername = "trainee.HealthUsername";
        String testPassword = "testpassword";

        try {
            boolean isAuthenticated = authenticationService.authenticate(testUsername, testPassword);

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