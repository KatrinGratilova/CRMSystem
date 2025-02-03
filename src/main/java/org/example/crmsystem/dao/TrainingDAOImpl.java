package org.example.crmsystem.dao;

import org.example.crmsystem.dao.interfaces.TrainingDAO;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.model.Training;
import org.example.crmsystem.storage.InMemoryTrainingStorage;
import org.example.crmsystem.storage.interfaces.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingDAOImpl implements TrainingDAO {
    private final Storage<Training> inMemoryTrainingStorage;

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

    @Override
    public Training update(Training training) throws EntityNotFoundException {
        return inMemoryTrainingStorage.update(training);
    }

    @Override
    public List<Training> getByName(String name) {
        return inMemoryTrainingStorage.getByName(name);
    }
}
