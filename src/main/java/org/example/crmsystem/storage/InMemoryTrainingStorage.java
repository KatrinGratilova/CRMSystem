package org.example.crmsystem.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.model.Training;
import org.example.crmsystem.storage.interfaces.Storage;
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
public class InMemoryTrainingStorage implements Storage<Training> {
    private final ObjectMapper mapper;
    private final String trainingFilePath;
    private HashMap<Long, Training> trainingStorage;
    private long idCounter;

    public InMemoryTrainingStorage(ObjectMapper mapper, @Value("${file.training.path}") String trainingFilePath) {
        this.mapper = mapper;
        this.trainingFilePath = trainingFilePath;
    }

    @PostConstruct
    public void init() {
        log.debug("Initializing in-memory training storage from file: {}", trainingFilePath);
        try {
            trainingStorage = mapper.readValue(new File(trainingFilePath), new TypeReference<HashMap<Long, Training>>() {
            });
            idCounter = trainingStorage
                    .keySet()
                    .stream()
                    .max(Long::compare)
                    .orElse(0L);
            log.info("Training storage initialized successfully with {} records.", trainingStorage.size());
        } catch (Exception e) {
            log.error("Failed to initialize training storage from file: {}", trainingFilePath);
            trainingStorage = new HashMap<>();
        }
    }

    @Override
    public Training add(Training training) {
        training.setTrainingId(++idCounter);
        trainingStorage.put(idCounter, training);
        return training;
    }

    @Override
    public Optional<Training> getById(long id) {
        return Optional.ofNullable(trainingStorage.get(id));
    }

    @Override
    public Training update(Training training) throws EntityNotFoundException {
        long trainingId = training.getTrainingId();
        if (!trainingStorage.containsKey(trainingId))
            throw new EntityNotFoundException( "Training with ID " + trainingId + " does not exist.");

        trainingStorage.put(trainingId, training);
        return training;
    }

    @Override
    public boolean deleteById(long id) {
        return trainingStorage.remove(id) != null;
    }

    @Override
    public List<Training> getByName(String name) {
        log.debug("Searching trainings with username starting with '{}'", name);
        List<Training> trainings = trainingStorage.values()
                .stream()
                .filter(x -> x.getTrainingName().startsWith(name))
                .collect(Collectors.toList());

        log.info("Found {} trainings with username starting with '{}'.", trainings.size(), name);
        return trainings;
    }

    @Override
    public List<Training> getAll() {
        return trainingStorage.values().stream().toList();
    }

    @PreDestroy
    public void destroy() {
        log.debug("Saving training storage to file: {}", trainingFilePath);
        try {
            mapper.writeValue(new File(trainingFilePath), trainingStorage);
            log.info("Training storage saved successfully to file.");
        } catch (IOException e) {
            log.error("Failed to save training storage to file: {}", trainingFilePath, e);
        }
    }
}
