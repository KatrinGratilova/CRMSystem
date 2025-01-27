package org.example.crmsystem.service;

import org.example.crmsystem.dao.interfaces.TrainerRepository;
import org.example.crmsystem.model.Trainer;
import org.example.crmsystem.utils.PasswordGenerator;
import org.example.crmsystem.utils.UsernameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TrainerService {
    private final TrainerRepository trainerRepository;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;

    @Autowired
    public TrainerService(TrainerRepository trainerRepository, PasswordGenerator passwordGenerator, UsernameGenerator usernameGenerator) {
        this.trainerRepository = trainerRepository;
        this.passwordGenerator = passwordGenerator;
        this.usernameGenerator = usernameGenerator;
    }

    public Trainer add(Trainer trainer) {
        trainer.setUserName(usernameGenerator.generateUserName(trainer));
        trainer.setPassword(passwordGenerator.generateUserPassword());
        return trainerRepository.add(trainer);
    }

    public Trainer update(Trainer trainer) {
        return trainerRepository.update(trainer);
    }

    public Optional<Trainer> getById(long id) {
        return trainerRepository.getById(id);
    }
}
