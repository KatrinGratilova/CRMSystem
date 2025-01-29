package org.example.crmsystem.service;

import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.model.Trainee;
import org.example.crmsystem.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
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

    public Trainee add(Trainee trainee) throws IOException {
        trainee.setPassword(passwordGenerator.generateUserPassword());
        trainee.setUserName(usernameGenerator.generateUserName(trainee));
        return traineeDAO.add(trainee);
    }

    public Trainee update(Trainee trainee) {
        return traineeDAO.update(trainee);
    }

    public boolean deleteById(long id) {
        return traineeDAO.deleteById(id);
    }

    public Optional<Trainee> getById(long id) {
        return traineeDAO.getById(id);
    }

    public Map<Long, Trainee> getAllTrainees() {
        return traineeDAO.getAll();
    }

    public void addTraining(long traineeId, long trainingId) {
        traineeDAO.addTraining(traineeId, trainingId);
    }
}
