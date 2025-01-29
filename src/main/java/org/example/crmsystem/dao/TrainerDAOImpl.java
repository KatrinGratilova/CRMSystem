package org.example.crmsystem.dao;

import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.model.Trainer;
import org.example.crmsystem.inMemoryStorage.InMemoryTrainerStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TrainerDAOImpl implements TrainerDAO {
    private final InMemoryTrainerStorage inMemoryTrainerStorage;

    @Autowired
    public TrainerDAOImpl(InMemoryTrainerStorage inMemoryTrainerStorage) {
        this.inMemoryTrainerStorage = inMemoryTrainerStorage;
    }

    @Override
    public Trainer add(Trainer trainer) {
        return inMemoryTrainerStorage.add(trainer);
    }

    @Override
    public Trainer update(Trainer trainer) {
        return inMemoryTrainerStorage.update(trainer);
    }

    @Override
    public Optional<Trainer> getById(long id) {
        return inMemoryTrainerStorage.getById(id);
    }

    @Override
    public void addTraining(long trainerId, long trainingId) {
        inMemoryTrainerStorage.addTraining(trainerId, trainingId);
    }
}
