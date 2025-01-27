package org.example.crmsystem.service;

import org.example.crmsystem.dao.interfaces.TrainingRepository;
import org.example.crmsystem.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TrainingService {
    private final TrainingRepository trainingRepository;

    @Autowired
    public TrainingService(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    public Training add(Training training) {
        return trainingRepository.add(training);
    }

    public Optional<Training> getById(long id) {
        return trainingRepository.getById(id);
    }
}
