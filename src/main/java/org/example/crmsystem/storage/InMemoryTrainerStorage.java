package org.example.crmsystem.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.model.Trainer;
import org.example.crmsystem.storage.interfaces.Storage;
import org.example.crmsystem.storage.interfaces.TrainingAttending;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Log4j2
public class InMemoryTrainerStorage implements Storage<Trainer>, TrainingAttending<Trainer> {
    private final ObjectMapper mapper;
    private final String trainerFilePath;
    private HashMap<Long, Trainer> trainerStorage;
    private long idCounter;

    public InMemoryTrainerStorage(ObjectMapper mapper, @Value("${file.trainer.path}") String trainerFilePath) {
        this.mapper = mapper;
        this.trainerFilePath = trainerFilePath;
    }

    @PostConstruct
    public void init() {
        log.debug("Initializing in-memory trainer storage from file: {}", trainerFilePath);
        try {
            trainerStorage = mapper.readValue(new File(trainerFilePath), new TypeReference<HashMap<Long, Trainer>>() {
            });
            idCounter = trainerStorage
                    .keySet()
                    .stream()
                    .max(Long::compare)
                    .orElse(0L);
            log.info("Trainer storage initialized successfully with {} records.", trainerStorage.size());
        } catch (Exception e) {
            log.error("Failed to initialize trainer storage from file: {}", trainerFilePath);
            trainerStorage = new HashMap<>();
        }
    }

    @Override
    public Trainer add(Trainer trainer) {
        trainer.setTrainerId(++idCounter);
        trainerStorage.put(idCounter, trainer);
        return trainer;
    }

    @Override
    public Optional<Trainer> getById(long id) {
        return Optional.ofNullable(trainerStorage.get(id));
    }

    @Override
    public Trainer update(Trainer trainer) throws EntityNotFoundException {
        long trainerId = trainer.getTrainerId();
        if (!trainerStorage.containsKey(trainerId))
            throw new EntityNotFoundException( "Trainer with ID " + trainerId + " does not exist.");

        trainerStorage.put(trainerId, trainer);
        return trainer;
    }

    @Override
    public boolean deleteById(long id) {
        return trainerStorage.remove(id) != null;
    }

    @Override
    public List<Trainer> getByName(String userName) {
        log.debug("Searching trainers with username starting with '{}'", userName);
        List<Trainer> trainers = trainerStorage.values()
                .stream()
                .filter(x -> x.getUserName().startsWith(userName))
                .collect(Collectors.toList());

        log.info("Found {} trainers with username starting with '{}'.", trainers.size(), userName);
        return trainers;
    }

    @Override
    public List<Trainer> getAll() {
        return trainerStorage.values().stream().toList();
    }

    @Override
    public void addTraining(long trainerId, long trainingId) throws EntityNotFoundException {
        if (!trainerStorage.containsKey(trainerId))
            throw new EntityNotFoundException("Trainer with ID " + trainerId + " does not exist.");

        Trainer trainer = trainerStorage.get(trainerId);
        trainer.getTrainerTrainings().add(trainingId);
    }

    @PreDestroy
    public void destroy() {
        log.debug("Saving trainer storage to file: {}", trainerFilePath);
        try {
            mapper.writeValue(new File(trainerFilePath), trainerStorage);
            log.info("Trainer storage saved successfully to file.");
        } catch (IOException e) {
            log.error("Failed to save trainer storage to file: {}", trainerFilePath, e);
        }
    }
}
