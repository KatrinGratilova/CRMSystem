package org.example.crmsystem.dao;

import org.example.crmsystem.Storage;
import org.example.crmsystem.dao.interfaces.TraineeRepository;
import org.example.crmsystem.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class TraineeRepositoryImpl implements TraineeRepository {
    private Map<Long, Trainee> traineeStorage;
    private long idCounter;

    @Autowired
    public TraineeRepositoryImpl(Storage storage) {
        traineeStorage = storage.getTraineeStorage();
        idCounter = traineeStorage
                .keySet()
                .stream()
                .max(Long::compare)
                .orElse(0L);
    }

    @Override
    public Trainee add(Trainee trainee) {
        trainee.setUserId(++idCounter);
        traineeStorage.put(idCounter, trainee);
        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        if (!traineeStorage.containsKey(trainee.getUserId())) {
            throw new IllegalArgumentException("Trainee with ID " + trainee.getUserId() + " does not exist.");
        }
        traineeStorage.put(trainee.getUserId(), trainee);
        return trainee;
    }

    @Override
    public boolean deleteById(long id) {
        return traineeStorage.remove(id) != null;
    }

    @Override
    public Optional<Trainee> getById(long id) {
        return Optional.ofNullable(traineeStorage.get(id));
    }

    @Override
    public List<Trainee> getByUserName(String userName) {
        System.out.println(traineeStorage.getClass());
        return traineeStorage.values()
                .stream()
                .filter(x -> x.getUserName().startsWith(userName))
                .collect(Collectors.toList());
    }

    public Map<Long, Trainee> getAll() {
        return traineeStorage;
    }
}
