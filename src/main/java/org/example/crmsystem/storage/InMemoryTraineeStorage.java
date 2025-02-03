package org.example.crmsystem.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.model.Trainee;
import org.example.crmsystem.messages.LogMessages;
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
public class InMemoryTraineeStorage implements Storage<Trainee>, TrainingAttending<Trainee> {
    private final ObjectMapper mapper;
    private final String traineeFilePath;
    private HashMap<Long, Trainee> traineeStorage;
    private long idCounter;

    public InMemoryTraineeStorage(ObjectMapper mapper, @Value("${file.trainee.path}") String traineeFilePath) {
        this.mapper = mapper;
        this.traineeFilePath = traineeFilePath;
    }

    @PostConstruct
    public void init() {
        log.debug(LogMessages.INITIALIZING_STORAGE.getMessage(), traineeFilePath);
        try {
            traineeStorage = mapper.readValue(new File(traineeFilePath), new TypeReference<HashMap<Long, Trainee>>() {
            });
            idCounter = traineeStorage
                    .keySet()
                    .stream()
                    .max(Long::compare)
                    .orElse(0L);
            log.info(LogMessages.TRAINEE_STORAGE_INITIALIZED.getMessage(), traineeStorage.size());
        } catch (Exception e) {
            log.error(LogMessages.FAILED_TO_INITIALIZE_STORAGE.getMessage(), traineeFilePath);
            traineeStorage = new HashMap<>();
        }
    }

    @Override
    public Trainee add(Trainee trainee) {
        trainee.setTraineeId(++idCounter);
        traineeStorage.put(idCounter, trainee);
        return trainee;
    }

    @Override
    public Optional<Trainee> getById(long id) {
        return Optional.ofNullable(traineeStorage.get(id));
    }

    @Override
    public Trainee update(Trainee trainee) throws EntityNotFoundException {
        long traineeId = trainee.getTraineeId();
        if (!traineeStorage.containsKey(traineeId))
            throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(traineeId));

        traineeStorage.put(traineeId, trainee);
        return trainee;
    }

    @Override
    public boolean deleteById(long id) {
        return traineeStorage.remove(id) != null;
    }

    @Override
    public List<Trainee> getByName(String userName) {
        log.debug(LogMessages.SEARCHING_TRAINEES_BY_NAME.getMessage(), userName);
        List<Trainee> trainees = traineeStorage.values()
                .stream()
                .filter(x -> x.getUserName().startsWith(userName))
                .collect(Collectors.toList());

        log.info(LogMessages.FOUND_TRAINEES_BY_NAME.getMessage(), trainees.size(), userName);
        return trainees;
    }

    @Override
    public List<Trainee> getAll() {
        return traineeStorage.values().stream().toList();
    }

    @Override
    public void addTraining(long traineeId, long trainingId) throws EntityNotFoundException {
        if (!traineeStorage.containsKey(traineeId))
            throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(traineeId));

        Trainee trainee = traineeStorage.get(traineeId);
        trainee.getTraineeTrainings().add(trainingId);
    }

    @PreDestroy
    public void destroy() {
        log.debug(LogMessages.ATTEMPTING_TO_SAVE_STORAGE.getMessage(), traineeFilePath);
        try {
            mapper.writeValue(new File(traineeFilePath), traineeStorage);
            log.info(LogMessages.SAVED_STORAGE.getMessage(), traineeFilePath);
        } catch (IOException e) {
            log.error(LogMessages.FAILED_TO_SAVE_STORAGE.getMessage(), traineeFilePath, e);
        }
    }
}
