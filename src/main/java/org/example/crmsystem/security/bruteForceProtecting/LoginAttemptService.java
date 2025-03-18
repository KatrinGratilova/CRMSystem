package org.example.crmsystem.security.bruteForceProtecting;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {
    private static final int MAX_ATTEMPTS = 3;
    private static final long BLOCK_DURATION_MINUTES = 1;

    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> blockTime = new ConcurrentHashMap<>();

    public void loginFailed(String username) {
        int currentAttempts = attempts.getOrDefault(username, 0);
        attempts.put(username, currentAttempts + 1);

        if (currentAttempts + 1 >= MAX_ATTEMPTS) {
            blockTime.put(username, LocalDateTime.now());
        }
    }

    public void loginSucceeded(String username) {
        attempts.remove(username);
        blockTime.remove(username);
    }

    public boolean isBlocked(String username) {
        if (!blockTime.containsKey(username)) return false;

        LocalDateTime blockedAt = blockTime.get(username);
        if (blockedAt.plusMinutes(BLOCK_DURATION_MINUTES).isBefore(LocalDateTime.now())) {
            // unblock
            attempts.remove(username);
            blockTime.remove(username);
            return false;
        }
        return true;
    }
}
