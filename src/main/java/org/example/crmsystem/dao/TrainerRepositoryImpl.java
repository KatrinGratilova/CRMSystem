package org.example.crmsystem.dao;

import org.example.crmsystem.Storage;
import org.example.crmsystem.dao.interfaces.TrainerRepository;
import org.example.crmsystem.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Optional;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {
    private final HashMap<Long, Trainer> trainerStorage;
    private long idCounter;

    @Autowired
    public TrainerRepositoryImpl(Storage storage) {
        trainerStorage = storage.getTrainerStorage();
        idCounter = trainerStorage
                .keySet()
                .stream()
                .max(Long::compare)
                .orElse(0L);
    }

    @Override
    public Trainer add(Trainer trainer) {
        return trainerStorage.put(++idCounter, trainer);
    }

    @Override
    public Trainer update(Trainer trainer) {
        return trainerStorage.put(idCounter, trainer);
    }

    @Override
    public Optional<Trainer> getById(long id) {
        return Optional.ofNullable(trainerStorage.get(id));
    }
}
