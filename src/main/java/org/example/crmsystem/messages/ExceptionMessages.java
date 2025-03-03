package org.example.crmsystem.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionMessages {
    TRAINEE_NOT_FOUND("Trainee with username %s not found."),
    USER_NOT_FOUND("User with username %s not found."),
    TRAINER_NOT_FOUND("Trainer with username %s not found."),

    INCOMPATIBLE_SPECIALIZATION("Incompatible specialization for trainer with username %s while adding training with name %s."),
    USER_IS_NOT_AUTHENTICATED_WITH_USERNAME("User with username %s is not authenticated."),

    TRAINEE_WITH_USERNAME_IS_NOT_FOUND("Trainee with username %s is not found."),
    TRAINER_WITH_USERNAME_IS_NOT_FOUND("Trainer with username %s is not found."),

    INVALID_REQUEST_BODY("Request body is invalid. Request body is required and cannot be empty.");

    private final String message;

    public String format(Object... params) {
        return String.format(message, params);
    }
}
