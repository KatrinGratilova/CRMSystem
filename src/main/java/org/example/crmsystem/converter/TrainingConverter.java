package org.example.crmsystem.converter;

import org.example.crmsystem.dto.training.*;
import org.example.crmsystem.entity.TrainingEntity;
import jakarta.persistence.EntityNotFoundException;
import org.example.crmsystem.exception.UserIsNotAuthenticated;
import org.example.crmsystem.service.TraineeService;
import org.example.crmsystem.service.TrainerService;
import org.springframework.stereotype.Component;

@Component
public class TrainingConverter {
    private static TraineeService traineeService;
    private static TrainerService trainerService;

    public TrainingConverter(TraineeService traineeService, TrainerService trainerService) {
        TrainingConverter.traineeService = traineeService;
        TrainingConverter.trainerService = trainerService;
    }

    public static TrainingServiceDTO toServiceDTO(TrainingAddRequestDTO training) throws UserIsNotAuthenticated, EntityNotFoundException {
        return TrainingServiceDTO.builder()
                .trainee(TraineeConverter.toWithoutListDTO(traineeService.getByUsername(training.getTraineeUsername())))
                .trainer(TrainerConverter.toWithoutListDTO(trainerService.getByUsername(training.getTrainerUsername())))
                .trainingType(training.getTrainingType())
                .trainingName(training.getTrainingName())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .build();
    }

    public static TrainingServiceDTO toServiceDTO(TrainingEntity training) {
        return TrainingServiceDTO.builder()
                .id(training.getId())
                .trainee(TraineeConverter.toWithoutListDTO(training.getTrainee()))
                .trainer(TrainerConverter.toWithoutListDTO(training.getTrainer()))
                .trainingType(training.getTrainingType())
                .trainingName(training.getTrainingName())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .build();
    }

    public static TrainingWithoutUsersServiceDTO toWithoutUsersDTO(TrainingEntity training) {
        return TrainingWithoutUsersServiceDTO
                .builder()
                .id(training.getId())
                .traineeId(training.getTrainee().getId())
                .trainerId(training.getTrainer().getId())
                .trainingType(training.getTrainingType())
                .trainingName(training.getTrainingName())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .build();
    }

    public static TrainingByTraineeDTO toByTraineeDTO(TrainingEntity training) {
        return TrainingByTraineeDTO.builder()
                .trainerUsername(training.getTrainer().getUsername())
                .trainingType(training.getTrainingType())
                .trainingName(training.getTrainingName())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .build();
    }

    public static TrainingByTrainerDTO toByTrainerDTO(TrainingEntity training) {
        return TrainingByTrainerDTO.builder()
                .traineeUsername(training.getTrainee().getUsername())
                .trainingName(training.getTrainingName())
                .trainingType(training.getTrainingType())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .build();
    }

    public static TrainingEntity toEntity(TrainingServiceDTO training) {
        return TrainingEntity.builder()
                .id(training.getId())
                .trainee(TraineeConverter.toEntity(training.getTrainee()))
                .trainer(TrainerConverter.toEntity(training.getTrainer()))
                .trainingType(training.getTrainingType())
                .trainingName(training.getTrainingName())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .build();
    }

    public static TrainingEntity toEntity(TrainingWithoutUsersServiceDTO training) {
        return TrainingEntity.builder()
                .id(training.getId())
                .trainingType(training.getTrainingType())
                .trainingName(training.getTrainingName())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .build();
    }
}
