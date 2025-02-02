package org.example.crmsystem.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogMessages {
    TRAINER_NOT_FOUND("Trainer with ID {} not found."),
    TRAINEE_NOT_FOUND("Trainee with ID {} not found."),
    TRAINING_NOT_FOUND("Training with ID {} not found."),
    INITIALIZING_STORAGE("Initializing in-memory storage from file: {}");
    private final String message;
}
