package org.example.crmsystem.service;

import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.dao.interfaces.TrainingDAO;
import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.exception.*;
import org.example.crmsystem.messages.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
public class TrainingService {
    private final TrainingDAO trainingRepository;
    private final TrainerService trainerService;

    @Autowired
    public TrainingService(TrainingDAO trainingRepository, TrainerService trainerService) {
        this.trainingRepository = trainingRepository;
        this.trainerService = trainerService;
    }

    public TrainingEntity add(TrainingEntity trainingEntity) {
        log.debug(LogMessages.ATTEMPTING_TO_ADD_NEW_TRAINING.getMessage(), trainingEntity.getTrainingName());

        trainingEntity = trainingRepository.add(trainingEntity);

        log.info(LogMessages.ADDED_NEW_TRAINING.getMessage(), trainingEntity.getId());
        return trainingEntity;
    }

    public TrainingEntity getById(long id) throws EntityNotFoundException {
        log.debug(LogMessages.RETRIEVING_TRAINING.getMessage(), id);
        Optional<TrainingEntity> training = trainingRepository.getById(id);

        if (training.isEmpty()) {
            log.warn(LogMessages.TRAINING_NOT_FOUND.getMessage(), id);
            throw new EntityNotFoundException(ExceptionMessages.TRAINING_NOT_FOUND.format(id));
        } else {
            log.info(LogMessages.TRAINING_FOUND.getMessage(), id);
            return training.get();
        }
    }

    public TrainingEntity update(TrainingEntity trainingEntity) throws EntityNotFoundException, IncompatibleSpecialization, UserIsNotAuthenticated {
        log.debug(LogMessages.ATTEMPTING_TO_UPDATE_TRAINING.getMessage(), trainingEntity.getId());
        long trainingId = trainingEntity.getId();
        long trainerId = trainingEntity.getTrainer().getId();

        if (!trainerService.getById(trainerId).getSpecialization().equals(trainingEntity.getTrainingType())) {
            log.error(LogMessages.INCOMPATIBLE_SPECIALIZATION.getMessage(), trainerId, trainingId);
            throw new IncompatibleSpecialization(ExceptionMessages.INCOMPATIBLE_SPECIALIZATION.format(trainerId, trainingId));
        }

        TrainingEntity updatedTrainingEntity;
        try {
            updatedTrainingEntity = trainingRepository.update(trainingEntity);
        } catch (EntityNotFoundException e) {
            log.warn(LogMessages.TRAINING_NOT_FOUND.getMessage(), trainingId);
            throw e;
        }

        log.info(LogMessages.UPDATED_TRAINING.getMessage(), updatedTrainingEntity.getId());
        return updatedTrainingEntity;
    }
}
