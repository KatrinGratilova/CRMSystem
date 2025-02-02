package org.example.crmsystem.service;

import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.exception.EntityNotFoundException;
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
        log.debug("Attempting to add new trainer with name: {}", trainer.getFirstName());

        trainer.setUserName(usernameGenerator.generateUserName(trainer));
        trainer.setPassword(passwordGenerator.generateUserPassword());
        trainer.setTrainerTrainings(new ArrayList<>());
        Trainer savedTrainer = trainerDAO.add(trainer);
        log.info("Trainer with ID {} added successfully.", savedTrainer.getTrainerId());
        return savedTrainer;
    }

    public Trainer getById(long id) throws EntityNotFoundException {
        log.debug("Retrieving trainer with ID: {}", id);

        Optional<Trainer> trainer = trainerDAO.getById(id);
        if (trainer.isEmpty()) {
            log.error(LogMessages.TRAINER_NOT_FOUND.getMessage(), id);
            throw new EntityNotFoundException("Trainer with ID " + id + " is not found.");
        } else {
            log.info("Trainer with ID {} retrieved successfully.", id);
            return trainer.get();
        }
    }

    public Trainer update(Trainer trainer) throws EntityNotFoundException {
        log.debug("Attempting to update trainer with ID: {}", trainer.getTrainerId());

        Trainer updatedTrainer;
        try {
            updatedTrainer = trainerDAO.update(trainer);
        } catch (EntityNotFoundException e) {
            log.warn("Trainer with ID {} not found for update", trainer.getTrainerId());
            throw e;
        }
        log.info("Trainer with ID {} updated successfully.", updatedTrainer.getTrainerId());
        return updatedTrainer;
    }

    public void addTraining(long trainerId, long trainingId) throws EntityNotFoundException {
        trainerDAO.addTraining(trainerId, trainingId);
        log.info("Training with ID {} added to trainer with ID {}.", trainingId, trainerId);
    }
}
