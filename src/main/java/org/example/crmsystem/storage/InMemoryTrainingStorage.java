package org.example.crmsystem.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.model.Training;
import org.example.crmsystem.messages.LogMessages;
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
        log.debug(LogMessages.INITIALIZING_STORAGE.getMessage(), trainingFilePath);
        try {
            trainingStorage = mapper.readValue(new File(trainingFilePath), new TypeReference<HashMap<Long, Training>>() {
            });
            idCounter = trainingStorage
                    .keySet()
                    .stream()
                    .max(Long::compare)
                    .orElse(0L);
            log.info(LogMessages.TRAINING_STORAGE_INITIALIZED.getMessage(), trainingStorage.size());
        } catch (Exception e) {
            log.error(LogMessages.FAILED_TO_INITIALIZE_STORAGE.getMessage(), trainingFilePath);
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
            throw new EntityNotFoundException(ExceptionMessages.TRAINING_NOT_FOUND.format(trainingId));

        trainingStorage.put(trainingId, training);
        return training;
    }

    @Override
    public boolean deleteById(long id) {
        return trainingStorage.remove(id) != null;
    }

    @Override
    public List<Training> getByName(String name) {
        log.debug(LogMessages.SEARCHING_TRAININGS_BY_NAME.getMessage(), name);
        List<Training> trainings = trainingStorage.values()
                .stream()
                .filter(x -> x.getTrainingName().startsWith(name))
                .collect(Collectors.toList());

        log.info(LogMessages.FOUND_TRAININGS_BY_NAME.getMessage(), trainings.size(), name);
        return trainings;
    }

    @Override
    public List<Training> getAll() {
        return trainingStorage.values().stream().toList();
    }

    @PreDestroy
    public void destroy() {
        log.debug(LogMessages.ATTEMPTING_TO_SAVE_STORAGE.getMessage(), trainingFilePath);
        try {
            mapper.writeValue(new File(trainingFilePath), trainingStorage);
            log.info(LogMessages.SAVED_STORAGE.getMessage(), trainingFilePath);
        } catch (IOException e) {
            log.error(LogMessages.FAILED_TO_SAVE_STORAGE.getMessage(), trainingFilePath, e);
        }
    }
}
