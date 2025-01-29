package org.example.crmsystem.inMemoryStorage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.example.crmsystem.model.Trainee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InMemoryTraineeStorage {
    private final ObjectMapper mapper;
    private final String traineeFilePath;
    private HashMap<Long, Trainee> traineeStorage;
    private long idCounter;

    public InMemoryTraineeStorage(ObjectMapper mapper, @Value("${file.trainee.path}") String traineeFilePath) {
        this.mapper = mapper;
        this.traineeFilePath = traineeFilePath;
        this.traineeStorage = new HashMap<>();
    }

    @PostConstruct
    public void init() throws IOException {
        traineeStorage = mapper.readValue(new File(traineeFilePath), new TypeReference<HashMap<Long, Trainee>>() {
        });
        idCounter = traineeStorage
                .keySet()
                .stream()
                .max(Long::compare)
                .orElse(0L);
    }

    //@Override
    public Trainee add(Trainee trainee) {
        trainee.setTraineeId(++idCounter);
        traineeStorage.put(idCounter, trainee);
        return trainee;
    }

    //@Override
    public Trainee update(Trainee trainee) {
        if (!traineeStorage.containsKey(trainee.getTraineeId())) {
            throw new IllegalArgumentException("Trainee with ID " + trainee.getTraineeId() + " does not exist.");
        }
        traineeStorage.put(trainee.getTraineeId(), trainee);
        return trainee;
    }

    //@Override
    public boolean deleteById(long id) {
        return traineeStorage.remove(id) != null;
    }

    //@Override
    public Optional<Trainee> getById(long id) {
        return Optional.ofNullable(traineeStorage.get(id));
    }

    // @Override
    public List<Trainee> getByUserName(String userName) {
        return traineeStorage.values()
                .stream()
                .filter(x -> x.getUserName().startsWith(userName))
                .collect(Collectors.toList());
    }

    public Map<Long, Trainee> getAll() {
        return traineeStorage;
    }

    public void addTraining(long traineeId, long trainingId){
        getById(traineeId).ifPresent(trainee -> trainee.getTraineeTrainings().add(trainingId));
    }

    @PreDestroy
    public void destroy() throws IOException {
        mapper.writeValue(new File(traineeFilePath), traineeStorage);
    }
}
