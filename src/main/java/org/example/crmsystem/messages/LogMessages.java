package org.example.crmsystem.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogMessages {
    TRAINEE_NOT_FOUND("TraineeEntity with ID {} not found."),
    TRAINEE_NOT_FOUND_BY_USERNAME("TraineeEntity with userName {} not found."),
    TRAINER_NOT_FOUND("TrainerEntity with ID {} not found."),
    TRAINER_NOT_FOUND_BY_USERNAME("TrainerEntity with userName {} not found."),
    TRAINING_NOT_FOUND("TrainingEntity with ID {} not found."),

    TRAINEE_FOUND("TraineeEntity with ID {} retrieved successfully."),
    TRAINER_FOUND("TrainerEntity with ID {} retrieved successfully."),
    TRAINING_FOUND("TrainingEntity with ID {} retrieved successfully."),

    ATTEMPTING_TO_ADD_NEW_TRAINEE("Attempting to createProfile a new trainee with first name: {}."),
    ATTEMPTING_TO_ADD_NEW_TRAINER("Attempting to createProfile a new trainer with first name: {}."),
    ATTEMPTING_TO_ADD_NEW_TRAINING("Attempting to createProfile a new training with name: {}."),

    ADDED_NEW_TRAINEE("New trainee added with ID: {}."),
    ADDED_NEW_TRAINER("New trainer added with ID: {}."),
    ADDED_NEW_TRAINING("New training added with ID: {}."),

    RETRIEVING_TRAINEE("Retrieving trainee with ID: {}."),
    RETRIEVING_TRAINEE_BY_USERNAME("Retrieving trainee with userName: {}."),
    RETRIEVING_TRAINER_BY_USERNAME("Retrieving trainer with userName: {}."),
    RETRIEVING_TRAINER("Retrieving trainer with ID: {}."),
    RETRIEVING_TRAINING("Retrieving training with ID: {}."),

    ATTEMPTING_TO_UPDATE_TRAINEE("Attempting to update trainee with ID: {}"),
    ATTEMPTING_TO_UPDATE_TRAINER("Attempting to update trainer with ID: {}"),
    ATTEMPTING_TO_UPDATE_TRAINING("Attempting to update training with ID: {}"),

    UPDATED_TRAINEE("TraineeEntity with ID {} updated successfully."),
    UPDATED_TRAINER("TrainerEntity with ID {} updated successfully."),
    UPDATED_TRAINING("TrainingEntity with ID {} updated successfully."),

    ASSIGNED_TRAINING_TO_TRAINEE("TrainingEntity with ID {} added to trainee with ID {}."),
    ASSIGNED_TRAINING_TO_TRAINER("TrainingEntity with ID {} added to trainer with ID {}."),

    ATTEMPTING_TO_DELETE_TRAINEE("Attempting to delete trainee with ID: {}"),
    ATTEMPTING_TO_DELETE_TRAINEE_BY_USERNAME("Attempting to delete trainee with userName: {}"),
    DELETED_TRAINEE("TraineeEntity with ID {} deleted successfully."),
    DELETED_TRAINEE_BY_USERNAME("TraineeEntity with username {} deleted successfully."),

    INCOMPATIBLE_SPECIALIZATION("Incompatible specialization for trainer with ID {} while adding training with ID {}"),

    INITIALIZING_STORAGE("Initializing in-memory storage from file: {}"),
    FAILED_TO_INITIALIZE_STORAGE("Failed to initialize storage from file: {}"),
    TRAINEE_STORAGE_INITIALIZED("TraineeEntity storage initialized successfully with {} records."),
    TRAINER_STORAGE_INITIALIZED("TrainerEntity storage initialized successfully with {} records."),
    TRAINING_STORAGE_INITIALIZED("TrainingEntity storage initialized successfully with {} records."),

    ATTEMPTING_TO_SAVE_STORAGE("Saving storage to file: {}"),
    SAVED_STORAGE("Storage saved successfully to file: {}."),
    FAILED_TO_SAVE_STORAGE("Failed to save storage to file: {}."),

    SEARCHING_TRAINEES_BY_NAME("Searching trainees with username starting with {}"),
    SEARCHING_TRAINERS_BY_NAME("Searching trainers with username starting with {}"),
    SEARCHING_TRAININGS_BY_NAME("Searching trainings with name starting with {}"),

    FOUND_TRAINEES_BY_NAME("Found {} trainees with username starting with {}."),
    FOUND_TRAINERS_BY_NAME("Found {} trainers with username starting with {}."),
    FOUND_TRAININGS_BY_NAME("Found {} trainings with username starting with {}."),

    USER_WAS_AUTHENTICATED("User with userName {} was authenticated successfully."),
    USER_IS_AUTHENTICATED("User with id {} is authenticated."),
    USER_IS_AUTHENTICATED_BY_USERNAME("User with userName {} is authenticated."),
    USER_IS_NOT_AUTHENTICATED("User with id {} is not authenticated."),
    USER_IS_NOT_AUTHENTICATED_BY_USERNAME("User with userName {} is not authenticated."),
    USER_DOES_NOT_EXIST("User with userName {} does not exist."),

    ATTEMPTING_TO_CHANGE_TRAINEES_PASSWORD("Attempting to change trainees password with ID: {}"),
    ATTEMPTING_TO_CHANGE_TRAINERS_PASSWORD("Attempting to change trainers password with ID: {}"),
    ATTEMPTING_TO_CHANGE_USER_PASSWORD("Attempting to change user password with username: {}"),

    TRAINEES_PASSWORD_CHANGED("Trainees password with id {} changed successfully."),
    TRAINERS_PASSWORD_CHANGED("Trainers password with id {} changed successfully."),
    USER_PASSWORD_CHANGED("User password with username {} changed successfully."),
    TRAINEES_PASSWORD_NOT_CHANGED("Trainee password with id {} wasn't changed."),
    TRAINERS_PASSWORD_NOT_CHANGED("Trainers password with id {} wasn't changed."),
    USER_PASSWORD_NOT_CHANGED("User password with username {} wasn't changed."),

    ATTEMPTING_TO_CHANGE_TRAINEES_STATUS("Attempting to change trainees status with ID: {}"),
    ATTEMPTING_TO_CHANGE_TRAINERS_STATUS("Attempting to change trainers status with ID: {}"),

    TRAINEES_STATUS_CHANGED("Trainees status with id {} changed successfully to {}."),
    TRAINERS_STATUS_CHANGED("Trainers status with id {} changed successfully to {}."),

    FETCHING_TRAINERS_NOT_ASSIGNED_TO_TRAINEE("Fetching trainers not assigned to trainee with username: {}"),
    FOUND_TRAINERS_NOT_ASSIGNED_TO_TRAINEE("Found {} trainers not assigned to trainee with username: {}"),

    FETCHING_TRAININGS_FOR_TRAINEE_BY_CRITERIA("Fetching trainings for trainee: {}, from: {}, to: {}, trainer: {}, trainingType: {}"),
    FETCHING_TRAININGS_FOR_TRAINER_BY_CRITERIA("Fetching trainings for trainer: {}, from: {}, to: {}, trainer: {}"),
    FOUND_TRAININGS_FOR_TRAINEE("Found {} trainings for trainee: {}"),
    FOUND_TRAININGS_FOR_TRAINER("Found {} trainings for trainer: {}");


    private final String message;
}
