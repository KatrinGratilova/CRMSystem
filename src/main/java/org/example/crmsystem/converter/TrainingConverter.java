package org.example.crmsystem.converter;

import org.example.crmsystem.dto.training.TrainingAddRequestDTO;
import org.example.crmsystem.dto.training.TrainingServiceDTO;
import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.exception.EntityNotFoundException;
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
                .trainee(traineeService.getByUsername(training.getTraineeUsername()))
                .trainer(trainerService.getByUsername(training.getTrainerUsername()))
                .trainingType(training.getTrainingType())
                .trainingName(training.getTrainingName())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .build();
    }

    public static TrainingServiceDTO toServiceDTO(TrainingEntity training){
        return TrainingServiceDTO.builder()
                .id(training.getId())
                .trainee(TraineeConverter.toServiceDTO(training.getTrainee()))
                .trainer(TrainerConverter.toServiceDTO(training.getTrainer()))
                .trainingType(training.getTrainingType())
                .trainingName(training.getTrainingName())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .build();
    }

    public static TrainingEntity toEntity(TrainingServiceDTO training){
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
}
