package org.example.crmsystem.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionMessages {
    TRAINEE_NOT_FOUND("TraineeEntity with ID %d not found."),
    TRAINEE_NOT_FOUND_BY_USERNAME("TraineeEntity with userName %s not found."),
    USER_NOT_FOUND_BY_USERNAME("User with username %s not found."),
    TRAINER_NOT_FOUND("TrainerEntity with ID %d not found."),
    TRAINER_NOT_FOUND_BY_USERNAME("TrainerEntity with userName %s not found."),
    TRAINING_NOT_FOUND("TrainingEntity with ID %d not found."),

    CANNOT_UPDATE_TRAINING("Cannot update training: trainee or trainer with such ID is not found."),
    INCOMPATIBLE_SPECIALIZATION("Incompatible specialization for trainer with ID %d while adding training with ID %d."),
    USER_IS_NOT_AUTHENTICATED_WITH_USERNAME("User with userName %s is not authenticated."),

    TRAINEE_WITH_USERNAME_IS_NOT_FOUND("Trainee with userName %s is not found."),
    TRAINER_WITH_USERNAME_IS_NOT_FOUND("Trainer with userName %s is not found."),

    INVALID_REQUEST_BODY("Request body is invalid. Request body is required and cannot be empty.");

    private final String message;

    public String format(Object... params) {
        return String.format(message, params);
    }
}
