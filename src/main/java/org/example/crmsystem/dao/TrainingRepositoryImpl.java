package org.example.crmsystem.dao;

import org.example.crmsystem.Storage;
import org.example.crmsystem.dao.interfaces.TrainingRepository;
import org.example.crmsystem.model.Training;

import java.util.HashMap;
import java.util.Optional;

public class TrainingRepositoryImpl implements TrainingRepository {
    private final HashMap<Long, Training> trainingStorage;
    private long idCounter;

    public TrainingRepositoryImpl(Storage storage) {
        trainingStorage = storage.getTrainingStorage();
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
