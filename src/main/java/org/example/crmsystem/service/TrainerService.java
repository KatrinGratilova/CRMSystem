package org.example.crmsystem.service;

import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.messages.LogMessages;
import org.example.crmsystem.model.Trainer;
import org.example.crmsystem.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Log4j2
public class TrainerService {
    private final TrainerDAO trainerDAO;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;

    @Autowired
    public TrainerService(TrainerDAO trainerDAO, PasswordGenerator passwordGenerator, UsernameGenerator usernameGenerator) {
        this.trainerDAO = trainerDAO;
        this.passwordGenerator = passwordGenerator;
        this.usernameGenerator = usernameGenerator;
    }

    public Trainer add(Trainer trainer) {
        log.debug(LogMessages.ADDED_NEW_TRAINER.getMessage(), trainer.getFirstName());

        trainer.setUserName(usernameGenerator.generateUserName(trainer));
        trainer.setPassword(passwordGenerator.generateUserPassword());
        trainer.setTrainerTrainings(new ArrayList<>());
        Trainer savedTrainer = trainerDAO.add(trainer);
        log.info(LogMessages.ADDED_NEW_TRAINER.getMessage(), savedTrainer.getTrainerId());
        return savedTrainer;
    }

    public Trainer getById(long id) throws EntityNotFoundException {
        log.debug(LogMessages.RETRIEVING_TRAINER.getMessage(), id);

        Optional<Trainer> trainer = trainerDAO.getById(id);
        if (trainer.isEmpty()) {
            log.error(LogMessages.TRAINER_NOT_FOUND.getMessage(), id);
            throw new EntityNotFoundException(ExceptionMessages.TRAINER_NOT_FOUND.format(id));
        } else {
            log.info(LogMessages.TRAINER_FOUND.getMessage(), id);
            return trainer.get();
        }
    }

    public Trainer update(Trainer trainer) throws EntityNotFoundException {
        log.debug(LogMessages.ATTEMPTING_TO_UPDATE_TRAINER.getMessage(), trainer.getTrainerId());

        Trainer updatedTrainer;
        try {
            updatedTrainer = trainerDAO.update(trainer);
        } catch (EntityNotFoundException e) {
            log.warn(LogMessages.TRAINER_NOT_FOUND.getMessage(), trainer.getTrainerId());
            throw e;
        }
        log.info(LogMessages.UPDATED_TRAINER.getMessage(), updatedTrainer.getTrainerId());
        return updatedTrainer;
    }

    public void addTraining(long trainerId, long trainingId) throws EntityNotFoundException {
        trainerDAO.addTraining(trainerId, trainingId);
        log.info(LogMessages.ASSIGNED_TRAINING_TO_TRAINER.getMessage(), trainingId, trainerId);
    }
}
