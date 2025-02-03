package org.example.crmsystem.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogMessages {
    TRAINEE_NOT_FOUND("Trainee with ID {} not found."),
    TRAINER_NOT_FOUND("Trainer with ID {} not found."),
    TRAINING_NOT_FOUND("Training with ID {} not found."),

    TRAINEE_FOUND("Trainee with ID {} retrieved successfully."),
    TRAINER_FOUND("Trainer with ID {} retrieved successfully."),
    TRAINING_FOUND("Training with ID {} retrieved successfully."),

    ATTEMPTING_TO_ADD_NEW_TRAINEE("Attempting to add a new trainee with first name: {}."),
    ATTEMPTING_TO_ADD_NEW_TRAINER("Attempting to add a new trainer with first name: {}."),
    ATTEMPTING_TO_ADD_NEW_TRAINING("Attempting to add a new training with name: {}."),

    ADDED_NEW_TRAINEE("New trainee added with ID: {}."),
    ADDED_NEW_TRAINER("New trainer added with ID: {}."),
    ADDED_NEW_TRAINING("New training added with ID: {}."),

    RETRIEVING_TRAINEE("Retrieving trainee with ID: {}."),
    RETRIEVING_TRAINER("Retrieving trainer with ID: {}."),
    RETRIEVING_TRAINING("Retrieving training with ID: {}."),

    ATTEMPTING_TO_UPDATE_TRAINEE("Attempting to update trainee with ID: {}"),
    ATTEMPTING_TO_UPDATE_TRAINER("Attempting to update trainer with ID: {}"),
    ATTEMPTING_TO_UPDATE_TRAINING("Attempting to update training with ID: {}"),

    UPDATED_TRAINEE("Trainee with ID {} updated successfully."),
    UPDATED_TRAINER("Trainer with ID {} updated successfully."),
    UPDATED_TRAINING("Training with ID {} updated successfully."),

    ASSIGNED_TRAINING_TO_TRAINEE("Training with ID {} added to trainee with ID {}."),
    ASSIGNED_TRAINING_TO_TRAINER("Training with ID {} added to trainer with ID {}."),

    ATTEMPTING_TO_DELETE_TRAINEE("Attempting to delete trainee with ID: {}"),
    DELETED_TRAINEE("Trainee with ID {} deleted successfully."),

    INCOMPATIBLE_SPECIALIZATION("Incompatible specialization for trainer with ID {} while adding training with ID {}"),

    INITIALIZING_STORAGE("Initializing in-memory storage from file: {}"),
    FAILED_TO_INITIALIZE_STORAGE("Failed to initialize storage from file: {}"),
    TRAINEE_STORAGE_INITIALIZED("Trainee storage initialized successfully with {} records."),
    TRAINER_STORAGE_INITIALIZED("Trainer storage initialized successfully with {} records."),
    TRAINING_STORAGE_INITIALIZED("Training storage initialized successfully with {} records."),

    ATTEMPTING_TO_SAVE_STORAGE("Saving storage to file: {}"),
    SAVED_STORAGE("Storage saved successfully to file: {}."),
    FAILED_TO_SAVE_STORAGE("Failed to save storage to file: {}."),

    SEARCHING_TRAINEES_BY_NAME("Searching trainees with username starting with {}"),
    SEARCHING_TRAINERS_BY_NAME("Searching trainers with username starting with {}"),
    SEARCHING_TRAININGS_BY_NAME("Searching trainings with name starting with {}"),

    FOUND_TRAINEES_BY_NAME("Found {} trainees with username starting with {}."),
    FOUND_TRAINERS_BY_NAME("Found {} trainers with username starting with {}."),
    FOUND_TRAININGS_BY_NAME("Found {} trainings with username starting with {}.");

    private final String message;
}
