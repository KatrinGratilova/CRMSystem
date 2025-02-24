package org.example.crmsystem.facade;

import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.exception.IncompatibleSpecialization;
import org.example.crmsystem.exception.UserIsNotAuthenticated;
import org.example.crmsystem.service.TraineeService;
import org.example.crmsystem.service.TrainerService;
import org.example.crmsystem.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GymFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public GymFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public TraineeEntity createTraineeProfile(TraineeEntity traineeEntity) {
        return traineeService.createProfile(traineeEntity);
    }

    public TrainerEntity createTrainerProfile(TrainerEntity trainerEntity) {
        return trainerService.createProfile(trainerEntity);
    }

    public TrainingEntity createTrainingProfile(TrainingEntity trainingEntity) {
        return trainingService.add(trainingEntity);
    }

    public void planTraining(TrainingEntity trainingEntity, TraineeEntity traineeEntity, TrainerEntity trainerEntity) {
        try {
            trainingEntity.setTrainee(traineeEntity);
            trainingEntity.setTrainer(trainerEntity);

            trainingService.update(trainingEntity);

        } catch (EntityNotFoundException | IncompatibleSpecialization | UserIsNotAuthenticated ex) {
            System.out.println(ex.getMessage());
        }
    }
}
