package org.example.crmsystem.dao;

import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.model.Trainer;
import org.example.crmsystem.storage.InMemoryTrainerStorage;
import org.example.crmsystem.storage.interfaces.Storage;
import org.example.crmsystem.storage.interfaces.TrainingAttending;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDAOImpl implements TrainerDAO {
    private final Storage<Trainer> inMemoryTrainerStorage;
    private final TrainingAttending<Trainer> trainingAttending;

    @Autowired
    public TrainerDAOImpl(InMemoryTrainerStorage inMemoryTrainerStorage, TrainingAttending<Trainer> trainingAttending) {
        this.inMemoryTrainerStorage = inMemoryTrainerStorage;
        this.trainingAttending = trainingAttending;
    }

    @Override
    public Trainer add(Trainer trainer) {
        return inMemoryTrainerStorage.add(trainer);
    }

    @Override
    public Optional<Trainer> getById(long id) {
        return inMemoryTrainerStorage.getById(id);
    }

    @Override
    public Trainer update(Trainer trainer) throws EntityNotFoundException {
        return inMemoryTrainerStorage.update(trainer);
    }


    @Override
    public List<Trainer> getByUserName(String userName) {
        return inMemoryTrainerStorage.getByName(userName);
    }

    @Override
    public void addTraining(long trainerId, long trainingId) throws EntityNotFoundException {
        trainingAttending.addTraining(trainerId, trainingId);
    }
}
