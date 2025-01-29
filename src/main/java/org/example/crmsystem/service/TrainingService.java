package org.example.crmsystem.service;

import org.example.crmsystem.dao.interfaces.TrainingDAO;
import org.example.crmsystem.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TrainingService {
    private final TrainingDAO trainingDAO;
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    @Autowired
    public TrainingService(TrainingDAO trainingDAO, TraineeService traineeService, TrainerService trainerService) {
        this.trainingDAO = trainingDAO;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    public Training add(Training training) {
        Optional<Trainee> trainee = traineeService.getById(training.getTraineeId());
        Optional<Trainer> trainer = trainerService.getById(training.getTrainerId());

        if (trainee.isEmpty() || trainer.isEmpty()) {
            throw new RuntimeException();
        }

        training = trainingDAO.add(training);
        long trainingId = training.getTrainingId();

        traineeService.addTraining(trainee.get().getTraineeId(), trainingId);
        trainerService.addTraining(trainer.get().getTrainerId(), trainingId);

        return training;
    }

    public Optional<Training> getById(long id) {
        return trainingDAO.getById(id);
    }
}
