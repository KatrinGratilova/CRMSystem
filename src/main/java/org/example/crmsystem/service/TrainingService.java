package org.example.crmsystem.service;

import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.dao.interfaces.TrainingDAO;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.exception.IncompatibleSpecialization;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.messages.LogMessages;
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
        log.debug(LogMessages.ATTEMPTING_TO_ADD_NEW_TRAINING.getMessage(), training.getTrainingName());

        training = trainingDAO.add(training);

        log.info(LogMessages.ADDED_NEW_TRAINING.getMessage(), training.getTrainingId());
        return training;
    }

    public Training getById(long id) throws EntityNotFoundException {
        log.debug(LogMessages.RETRIEVING_TRAINING.getMessage(), id);
        Optional<Training> training = trainingDAO.getById(id);

        if (training.isEmpty()) {
            log.warn(LogMessages.TRAINING_NOT_FOUND.getMessage(), id);
            throw new EntityNotFoundException(ExceptionMessages.TRAINING_NOT_FOUND.format(id));
        } else {
            log.info(LogMessages.TRAINING_FOUND.getMessage(), id);
            return training.get();
        }
    }

    public Training update(Training training) throws EntityNotFoundException, IncompatibleSpecialization {
        long traineeId = training.getTraineeId();
        long trainerId = training.getTrainerId();
        long trainingId = training.getTrainingId();

        log.debug(LogMessages.ATTEMPTING_TO_UPDATE_TRAINING.getMessage(), trainingId);

        if (!checkIfTraineeExists(traineeId) || !checkIfTrainerExists(trainerId)) {
            throw new EntityNotFoundException(ExceptionMessages.CANNOT_UPDATE_TRAINING.getMessage());
        }
        if (!trainerService.getById(trainerId).getSpecialization().equals(training.getTrainingType())) {
            log.error(LogMessages.INCOMPATIBLE_SPECIALIZATION.getMessage(), trainerId, trainingId);
            throw new IncompatibleSpecialization(ExceptionMessages.INCOMPATIBLE_SPECIALIZATION.format(trainerId, trainingId));
        }

        Training updatedTraining;
        try {
            updatedTraining = trainingDAO.update(training);
        } catch (EntityNotFoundException e) {
            log.warn(LogMessages.TRAINING_NOT_FOUND.getMessage(), trainingId);
            throw e;
        }
        log.info(LogMessages.UPDATED_TRAINING.getMessage(), trainingId);
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
