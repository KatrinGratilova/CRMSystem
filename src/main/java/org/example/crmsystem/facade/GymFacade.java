package org.example.crmsystem.facade;

import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.exception.IncompatibleSpecialization;
import org.example.crmsystem.model.*;
import org.example.crmsystem.service.*;
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

    public Trainee createTraineeProfile(Trainee trainee) {
        return traineeService.add(trainee);
    }

    public Trainer createTrainerProfile(Trainer trainer) {
        return trainerService.add(trainer);
    }

    public Training createTrainingProfile(Training training) {
        return trainingService.add(training);
    }

    public void planTraining(long trainingId, long traineeId, long trainerId) {
        try {
            Training training = trainingService.getById(trainingId);
            training.setTraineeId(traineeId);
            training.setTrainerId(trainerId);
            trainingService.update(training);

            traineeService.addTraining(traineeId, trainingId);
            trainerService.addTraining(trainerId, trainingId);
        } catch (EntityNotFoundException | IncompatibleSpecialization ex) {
            System.out.println(ex.getMessage());
        }
    }
}
