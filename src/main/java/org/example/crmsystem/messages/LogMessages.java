package org.example.crmsystem.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogMessages {
    TRAINEE_NOT_FOUND("TransactionId: {}. Trainee with username {} not found."),
    TRAINER_NOT_FOUND("TransactionId: {}. Trainer with username {} not found."),
    TRAINING_NOT_FOUND("TransactionId: {}. Training with name {} not found."),

    TRAINEE_FOUND("TransactionId: {}. Trainee with username {} retrieved successfully."),
    TRAINER_FOUND("TransactionId: {}. Trainer with username {} retrieved successfully."),
    TRAINING_FOUND("TransactionId: {}. Training with name {} retrieved successfully."),

    ATTEMPTING_TO_ADD_NEW_TRAINEE("TransactionId: {}. Attempting to create a new trainee with first name: {}."),
    ATTEMPTING_TO_ADD_NEW_TRAINER("TransactionId: {}. Attempting to create a new trainer with first name: {}."),
    ATTEMPTING_TO_ADD_NEW_TRAINING("TransactionId: {}. Attempting to create a new training with name: {}."),

    ADDED_NEW_TRAINEE("TransactionId: {}. New trainee added with username: {}."),
    ADDED_NEW_TRAINER("TransactionId: {}. New trainer added with username: {}."),
    ADDED_NEW_TRAINING("TransactionId: {}. New training added with name: {}."),

    RETRIEVING_TRAINEE("TransactionId: {}. Retrieving trainee with username: {}."),
    RETRIEVING_TRAINER("TransactionId: {}. Retrieving trainer with username: {}."),
    RETRIEVING_TRAINING("Retrieving training with name: {}."),

    ATTEMPTING_TO_UPDATE_TRAINEE("TransactionId: {}. Attempting to update trainee with username: {}"),
    ATTEMPTING_TO_UPDATE_TRAINER("TransactionId: {}. Attempting to update trainer with username: {}"),

    UPDATED_TRAINEE("TransactionId: {}. Trainee with username {} updated successfully."),
    UPDATED_TRAINER("TransactionId: {}. Trainer with username {} updated successfully."),

    ASSIGNED_TRAINING_TO_TRAINEE("TransactionId: {}. Training with name {} added to trainee with username {}."),
    ASSIGNED_TRAINING_TO_TRAINER("TransactionId: {}. Training with name {} added to trainer with username {}."),

    ATTEMPTING_TO_DELETE_TRAINEE("TransactionId: {}. Attempting to delete trainee with username: {}"),
    DELETED_TRAINEE("TransactionId: {}. Trainee with username {} deleted successfully."),

    INCOMPATIBLE_SPECIALIZATION("TransactionId: {}. Incompatible specialization for trainer with username {} while adding training with name {}"),

    SEARCHING_TRAINEES_BY_NAME("TransactionId: {}. Searching trainees with username starting with {}"),
    SEARCHING_TRAINERS_BY_NAME("TransactionId: {}. Searching trainers with username starting with {}"),
    SEARCHING_TRAININGS_BY_NAME("TransactionId: {}. Searching trainings with name starting with {}"),

    FOUND_TRAINEES_BY_NAME("TransactionId: {}. Found {} trainees with username starting with {}."),
    FOUND_TRAINERS_BY_NAME("TransactionId: {}. Found {} trainers with username starting with {}."),
    FOUND_TRAININGS_BY_NAME("TransactionId: {}. Found {} trainings with username starting with {}."),

    USER_WAS_AUTHENTICATED("TransactionId: {}. User with username {} was authenticated successfully."),
    USER_IS_AUTHENTICATED("TransactionId: {}. User with username {} is authenticated."),
    USER_IS_NOT_AUTHENTICATED("TransactionId: {}. User with username {} is not authenticated."),
    USER_DOES_NOT_EXIST("TransactionId: {}. User with username {} does not exist."),

    ATTEMPTING_TO_CHANGE_USER_PASSWORD("TransactionId: {}. Attempting to change user password with username: {}"),
    USER_PASSWORD_CHANGED("TransactionId: {}. User password with username {} changed successfully."),
    USER_PASSWORD_NOT_CHANGED("TransactionId: {}. User password with username {} wasn't changed."),

    ATTEMPTING_TO_CHANGE_TRAINEES_STATUS("TransactionId: {}. Attempting to change trainees status with username: {}"),
    ATTEMPTING_TO_CHANGE_TRAINERS_STATUS("TransactionId: {}. Attempting to change trainers status with username: {}"),

    TRAINEES_STATUS_CHANGED("TransactionId: {}. Trainees status with username {} changed successfully to {}."),
    TRAINERS_STATUS_CHANGED("TransactionId: {}. Trainers status with username {} changed successfully to {}."),

    FETCHING_TRAINERS_NOT_ASSIGNED_TO_TRAINEE("TransactionId: {}. Fetching trainers not assigned to trainee with username: {}"),
    FOUND_TRAINERS_NOT_ASSIGNED_TO_TRAINEE("TransactionId: {}. Found {} trainers not assigned to trainee with username: {}"),

    FETCHING_TRAININGS_FOR_TRAINEE_BY_CRITERIA("TransactionId: {}. Fetching trainings for trainee: {}, from: {}, to: {}, trainer: {}, trainingType: {}"),
    FETCHING_TRAININGS_FOR_TRAINER_BY_CRITERIA("TransactionId: {}. Fetching trainings for trainer: {}, from: {}, to: {}, trainer: {}"),
    FOUND_TRAININGS_FOR_TRAINEE("TransactionId: {}. Found {} trainings for trainee: {}"),
    FOUND_TRAININGS_FOR_TRAINER("TransactionId: {}. Found {} trainings for trainer: {}"),

    ATTEMPTING_TO_UPDATE_TRAINEE_TRAINERS("TransactionId: {}. Attempting to update trainee trainers with username: {}"),
    TRAINEE_TRAINERS_UPDATED("TransactionId: {}. Trainee trainers with username {} updated successfully."),

    ERROR_OCCURRED("TransactionId: {}. Error occurred: {}");

    private final String message;
}
