package org.example.crmsystem.dao;

import org.example.crmsystem.dao.interfaces.TrainingDAO;
import org.example.crmsystem.model.Training;
import org.example.crmsystem.inMemoryStorage.InMemoryTrainingStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TrainingDAOImpl implements TrainingDAO {
    private final InMemoryTrainingStorage inMemoryTrainingStorage;

    @Autowired
    public TrainingDAOImpl(InMemoryTrainingStorage inMemoryTrainingStorage) {
        this.inMemoryTrainingStorage = inMemoryTrainingStorage;
    }

    @Override
    public Training add(Training training) {
        return inMemoryTrainingStorage.add(training);
    }

    @Override
    public Optional<Training> getById(long id) {
        return inMemoryTrainingStorage.getById(id);
    }
}
