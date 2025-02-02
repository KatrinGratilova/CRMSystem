package org.example.crmsystem.dao;

import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.model.Trainee;
import org.example.crmsystem.storage.InMemoryTraineeStorage;
import org.example.crmsystem.storage.interfaces.Storage;
import org.example.crmsystem.storage.interfaces.TrainingAttending;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TraineeDAOImpl implements TraineeDAO {
    private final Storage<Trainee> inMemoryTraineeStorage;
    private final TrainingAttending<Trainee> trainingAttending;

    @Autowired
    public TraineeDAOImpl(InMemoryTraineeStorage inMemoryTraineeStorage, TrainingAttending<Trainee> trainingAttending) {
        this.inMemoryTraineeStorage = inMemoryTraineeStorage;
        this.trainingAttending = trainingAttending;
    }

    @Override
    public Trainee add(Trainee trainee) {
        return inMemoryTraineeStorage.add(trainee);
    }

    @Override
    public Optional<Trainee> getById(long id) {
        return inMemoryTraineeStorage.getById(id);
    }

    @Override
    public Trainee update(Trainee trainee) throws EntityNotFoundException {
        return inMemoryTraineeStorage.update(trainee);
    }

    @Override
    public boolean deleteById(long id) {
        return inMemoryTraineeStorage.deleteById(id);
    }

    @Override
    public List<Trainee> getByUserName(String userName) {
        return inMemoryTraineeStorage.getByName(userName);
    }

    @Override
    public void addTraining(long traineeId, long trainingId) throws EntityNotFoundException {
        trainingAttending.addTraining(traineeId, trainingId);
    }
}
