package org.example.crmsystem.service;

import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.model.Trainer;
import org.example.crmsystem.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
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
        trainer.setUserName(usernameGenerator.generateUserName(trainer));
        trainer.setPassword(passwordGenerator.generateUserPassword());
        return trainerDAO.add(trainer);
    }

    public Trainer update(Trainer trainer) {
        return trainerDAO.update(trainer);
    }

    public Optional<Trainer> getById(long id) {
        return trainerDAO.getById(id);
    }

    public void addTraining(long trainerId, long trainingId) {
        trainerDAO.addTraining(trainerId, trainingId);
    }
}
