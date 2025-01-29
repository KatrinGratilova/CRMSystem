package org.example.crmsystem.dao;

import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.model.Trainee;
import org.example.crmsystem.inMemoryStorage.InMemoryTraineeStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TraineeDAOImpl implements TraineeDAO {
    private final InMemoryTraineeStorage inMemoryTraineeStorage;

    @Autowired
    public TraineeDAOImpl(InMemoryTraineeStorage inMemoryTraineeStorage) {
        this.inMemoryTraineeStorage = inMemoryTraineeStorage;
    }

    @Override
    public Trainee add(Trainee trainee) {
        return inMemoryTraineeStorage.add(trainee);
    }

    @Override
    public Trainee update(Trainee trainee) {
        return inMemoryTraineeStorage.update(trainee);
    }

    @Override
    public boolean deleteById(long id) {
        return inMemoryTraineeStorage.deleteById(id);
    }

    @Override
    public Optional<Trainee> getById(long id) {
        return inMemoryTraineeStorage.getById(id);
    }

    @Override
    public List<Trainee> getByUserName(String userName) {
        return inMemoryTraineeStorage.getByUserName(userName);
    }

    public Map<Long, Trainee> getAll() {
        return inMemoryTraineeStorage.getAll();
    }

    @Override
    public void addTraining(long traineeId, long trainingId) {
        inMemoryTraineeStorage.addTraining(traineeId, trainingId);
    }
}
