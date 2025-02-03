package org.example.crmsystem.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionMessages {
    TRAINEE_NOT_FOUND("Trainee with ID %d not found."),
    TRAINER_NOT_FOUND("Trainer with ID %d not found."),
    TRAINING_NOT_FOUND("Training with ID %d not found."),

    CANNOT_UPDATE_TRAINING("Cannot update training: trainee or trainer with such ID is not found."),
    INCOMPATIBLE_SPECIALIZATION("Incompatible specialization for trainer with ID %d while adding training with ID %d.");

    private final String message;

    public String format(Object... params) {
        return String.format(message, params);
    }
}
