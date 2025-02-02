package org.example.crmsystem.service;

import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.dao.interfaces.TrainingDAO;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.exception.IncompatibleSpecialization;
import org.example.crmsystem.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
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
        log.debug("Attempting to add training for trainee with ID {} and trainer with ID {}",
                training.getTraineeId(), training.getTrainerId());

        training = trainingDAO.add(training);

        return training;
    }

    public Training getById(long id) throws EntityNotFoundException {
        log.debug("Retrieving training with ID {}", id);
        Optional<Training> training = trainingDAO.getById(id);

        if (training.isEmpty()) {
            log.warn(LogMessages.TRAINING_NOT_FOUND.getMessage(), id);
            throw new EntityNotFoundException("Training with ID " + id + " is not found.");
        } else {
            log.info("Training with ID {} retrieved successfully.", id);
            return training.get();
        }
    }

    public Training update(Training training) throws EntityNotFoundException, IncompatibleSpecialization {
        long traineeId = training.getTraineeId();
        long trainerId = training.getTrainerId();
        long trainingId = training.getTrainingId();

        log.debug("Starting to update trainee with ID: {}", trainingId);


        if (!checkIfTraineeExists(traineeId) || !checkIfTrainerExists(trainerId)) {
            throw new EntityNotFoundException("Cannot create training: trainee or trainer with such ID is not found.");
        }
        if (!trainerService.getById(trainerId).getSpecialization().equals(training.getTrainingType())) {
            log.error("Incompatible specialization for trainer with ID {} while adding training with ID {}",
                    trainerId, trainingId);
            throw new IncompatibleSpecialization("Incompatible specialization for trainer with ID " + trainerId + " while adding training with ID" + trainingId);
        }

        Training updatedTraining;
        try {
            updatedTraining = trainingDAO.update(training);
        } catch (EntityNotFoundException e) {
            log.warn("Training with ID {} not found for update", trainingId);
            throw e;
        }
        log.info("Training successfully updated with ID: {}", updatedTraining.getTrainingId());
        return updatedTraining;
    }

    private boolean checkIfTraineeExists(long traineeId) {
        try {
            traineeService.getById(traineeId);
        } catch (EntityNotFoundException e) {
            return false;
        }
        return true;
    }

    private boolean checkIfTrainerExists(long trainerId) {
        try {
            trainerService.getById(trainerId);
        } catch (EntityNotFoundException e) {
            return false;
        }
        return true;
    }
}
