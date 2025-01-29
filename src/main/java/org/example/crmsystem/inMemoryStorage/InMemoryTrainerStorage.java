package org.example.crmsystem.inMemoryStorage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.example.crmsystem.model.Trainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

@Component
public class InMemoryTrainerStorage {
    private final ObjectMapper mapper;
    private final String trainerFilePath;
    private HashMap<Long, Trainer> trainerStorage;
    private long idCounter;

    public InMemoryTrainerStorage(ObjectMapper mapper, @Value("${file.trainer.path}") String trainerFilePath) {
        this.mapper = mapper;
        this.trainerFilePath = trainerFilePath;
        trainerStorage = new HashMap<>();
    }

    @PostConstruct
    public void init() throws IOException {
        trainerStorage = mapper.readValue(new File(trainerFilePath), new TypeReference<HashMap<Long, Trainer>>() {
        });
        idCounter = trainerStorage
                .keySet()
                .stream()
                .max(Long::compare)
                .orElse(0L);
    }

    //@Override
    public Trainer add(Trainer trainer) {
        trainer.setTrainerId(++idCounter);
        return trainerStorage.put(idCounter, trainer);
    }

    //@Override
    public Trainer update(Trainer trainer) {
        return trainerStorage.put(idCounter, trainer);
    }

    //@Override
    public Optional<Trainer> getById(long id) {
        return Optional.ofNullable(trainerStorage.get(id));
    }

    public void addTraining(long trainerId, long trainingId){
        getById(trainerId).ifPresent(trainee -> trainee.getTrainerTrainings().add(trainingId));
    }

    @PreDestroy
    public void destroy() throws IOException {
        mapper.writeValue(new File(trainerFilePath), trainerStorage);
    }
}
