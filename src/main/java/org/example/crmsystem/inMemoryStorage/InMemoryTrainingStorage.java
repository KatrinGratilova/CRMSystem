package org.example.crmsystem.inMemoryStorage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.example.crmsystem.model.Training;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

@Component
public class InMemoryTrainingStorage {
    private final ObjectMapper mapper;
    private final String trainingFilePath;
    private HashMap<Long, Training> trainingStorage;
    private long idCounter;

    public InMemoryTrainingStorage(ObjectMapper mapper, @Value("${file.training.path}") String trainingFilePath) {
        this.mapper = mapper;
        this.trainingFilePath = trainingFilePath;
        trainingStorage = new HashMap<>();
    }

    @PostConstruct
    public void init() throws IOException {
        trainingStorage = mapper.readValue(new File(trainingFilePath), new TypeReference<HashMap<Long, Training>>() {
        });
        idCounter = trainingStorage
                .keySet()
                .stream()
                .max(Long::compare)
                .orElse(0L);
    }

    //@Override
    public Training add(Training training) {
        training.setTrainingId(++idCounter);
        return trainingStorage.put(idCounter, training);
    }

    //@Override
    public Optional<Training> getById(long id) {
        return Optional.ofNullable(trainingStorage.get(id));
    }

    @PreDestroy
    public void destroy() throws IOException {
        mapper.writeValue(new File(trainingFilePath), trainingStorage);
    }
}
