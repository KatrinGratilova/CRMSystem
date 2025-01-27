package org.example.crmsystem.service;

import org.example.crmsystem.dao.interfaces.TraineeRepository;
import org.example.crmsystem.model.Trainee;
import org.example.crmsystem.utils.PasswordGenerator;
import org.example.crmsystem.utils.UsernameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
public class TraineeService {
    private final TraineeRepository traineeRepository;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;

    @Autowired
    public TraineeService(TraineeRepository traineeRepository, PasswordGenerator passwordGenerator, UsernameGenerator usernameGenerator) {
        this.traineeRepository = traineeRepository;
        this.passwordGenerator = passwordGenerator;
        this.usernameGenerator = usernameGenerator;
    }

    public Trainee add(Trainee trainee) throws IOException {
        trainee.setPassword(passwordGenerator.generateUserPassword());
        trainee.setUserName(usernameGenerator.generateUserName(trainee));
        return traineeRepository.add(trainee);
    }

    public Trainee update(Trainee trainee) {
        return traineeRepository.update(trainee);
    }

    public boolean deleteById(long id) {
        return traineeRepository.deleteById(id);
    }

    public Optional<Trainee> getById(long id) {
        return traineeRepository.getById(id);
    }

    public Map<Long, Trainee> getAllTrainees() {
        return traineeRepository.getAll();
    }
}
