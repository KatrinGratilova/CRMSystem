package org.example.crmsystem.service;

import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.exception.EntityNotFoundException;
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
        log.debug("Attempting to add a new trainee with first name: {}", trainee.getFirstName());

        trainee.setPassword(passwordGenerator.generateUserPassword());
        trainee.setUserName(usernameGenerator.generateUserName(trainee));
        trainee.setTraineeTrainings(new ArrayList<>());

        Trainee addedTrainee = traineeDAO.add(trainee);
        log.info("New trainee added with ID: {}", addedTrainee.getTraineeId());
        return addedTrainee;
    }

    public Trainee getById(long id) throws EntityNotFoundException {
        log.debug("Starting fetching trainee by ID: {}", id);
        Optional<Trainee> trainee = traineeDAO.getById(id);
        if (trainee.isEmpty()) {
            log.warn("Trainee with ID {} not found", id);
            throw new EntityNotFoundException("Trainee with ID " + id + " not found");
        } else {
            log.info("Trainee found with ID: {}", id);
            return trainee.get();
        }
    }

    public Trainee update(Trainee trainee) throws EntityNotFoundException {
        log.debug("Starting to update trainee with ID: {}", trainee.getTraineeId());

        Trainee updatedTrainee;
        try {
            updatedTrainee = traineeDAO.update(trainee);
        } catch (EntityNotFoundException e) {
            log.warn("Trainee with ID {} not found for update", trainee.getTraineeId());
            throw e;
        }
        log.info("Trainee successfully updated with ID: {}", updatedTrainee.getTraineeId());
        return updatedTrainee;
    }

    public boolean deleteById(long id) {
        log.debug("Starting deletion of trainee with ID: {}", id);
        boolean deleted = traineeDAO.deleteById(id);
        if (deleted)
            log.info("Trainee with ID {} successfully deleted", id);
        else
            log.warn("Trainee with ID {} not found for deletion", id);

        return deleted;
    }

    public void addTraining(long traineeId, long trainingId) throws EntityNotFoundException {
        traineeDAO.addTraining(traineeId, trainingId);
    }
}
