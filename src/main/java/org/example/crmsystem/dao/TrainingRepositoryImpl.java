package org.example.crmsystem.dao;

import org.example.crmsystem.utils.InMemoryGymStorage;
import org.example.crmsystem.dao.interfaces.TrainingRepository;
import org.example.crmsystem.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Optional;

@Repository
public class TrainingRepositoryImpl implements TrainingRepository {
    private final HashMap<Long, Training> trainingStorage;
    private long idCounter;

    @Autowired
    public TrainingRepositoryImpl(InMemoryGymStorage inMemoryGymStorage) {
        trainingStorage = inMemoryGymStorage.getTrainingStorage();
        idCounter = trainingStorage
                .keySet()
                .stream()
                .max(Long::compare)
                .orElse(0L);
    }

    @Override
    public Training add(Training training) {
        return trainingStorage.put(++idCounter, training);
    }

    @Override
    public Optional<Training> getById(long id) {
        return Optional.ofNullable(trainingStorage.get(id));
    }
}
