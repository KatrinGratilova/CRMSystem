package org.example.crmsystem.service;

import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.messages.LogMessages;
import org.example.crmsystem.model.Trainee;
import org.example.crmsystem.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Log4j2
public class TraineeService {
    private final TraineeDAO traineeDAO;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;


    @Autowired
    public TraineeService(TraineeDAO traineeDAO, PasswordGenerator passwordGenerator, UsernameGenerator usernameGenerator) {
        this.traineeDAO = traineeDAO;
        this.passwordGenerator = passwordGenerator;
        this.usernameGenerator = usernameGenerator;
    }

    public Trainee add(Trainee trainee) {
        log.debug(LogMessages.ATTEMPTING_TO_ADD_NEW_TRAINEE.getMessage(), trainee.getFirstName());

        trainee.setPassword(passwordGenerator.generateUserPassword());
        trainee.setUserName(usernameGenerator.generateUserName(trainee));
        trainee.setTraineeTrainings(new ArrayList<>());

        Trainee addedTrainee = traineeDAO.add(trainee);
        log.info(LogMessages.ADDED_NEW_TRAINEE.getMessage(), addedTrainee.getTraineeId());
        return addedTrainee;
    }

    public Trainee getById(long id) throws EntityNotFoundException {
        log.debug(LogMessages.RETRIEVING_TRAINEE.getMessage(), id);
        Optional<Trainee> trainee = traineeDAO.getById(id);
        if (trainee.isEmpty()) {
            log.warn(LogMessages.TRAINEE_NOT_FOUND.getMessage(), id);
            throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(id));
        } else {
            log.info(LogMessages.TRAINEE_FOUND.getMessage(), id);
            return trainee.get();
        }
    }

    public Trainee update(Trainee trainee) throws EntityNotFoundException {
        log.debug(LogMessages.ATTEMPTING_TO_UPDATE_TRAINEE.getMessage(), trainee.getTraineeId());

        Trainee updatedTrainee;
        try {
            updatedTrainee = traineeDAO.update(trainee);
        } catch (EntityNotFoundException e) {
            log.warn(LogMessages.TRAINEE_NOT_FOUND.getMessage(), trainee.getTraineeId());
            throw e;
        }
        log.info(LogMessages.UPDATED_TRAINEE.getMessage(), updatedTrainee.getTraineeId());
        return updatedTrainee;
    }

    public boolean deleteById(long id) {
        log.debug(LogMessages.ATTEMPTING_TO_DELETE_TRAINEE.getMessage(), id);
        boolean deleted = traineeDAO.deleteById(id);
        if (deleted)
            log.info(LogMessages.DELETED_TRAINEE.getMessage(), id);
        else
            log.warn(LogMessages.TRAINEE_NOT_FOUND.getMessage(), id);

        return deleted;
    }

    public void addTraining(long traineeId, long trainingId) throws EntityNotFoundException {
        traineeDAO.addTraining(traineeId, trainingId);
        log.info(LogMessages.ASSIGNED_TRAINING_TO_TRAINEE.getMessage(), trainingId, traineeId);
    }
}
