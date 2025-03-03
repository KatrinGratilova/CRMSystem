package org.example.crmsystem.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.crmsystem.dao.TrainingTypeRepository;
import org.example.crmsystem.entity.TrainingType;
import org.example.crmsystem.entity.TrainingTypeEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingTypeService {
    private final TrainingTypeRepository trainingTypeRepository;

    @PostConstruct
    public void initTrainingTypes() {
        Arrays.stream(TrainingType.values())
                .forEach(type -> trainingTypeRepository.add(new TrainingTypeEntity(0, type)));
    }

    public TrainingTypeEntity getById(int id) {
        return trainingTypeRepository.getById(id);
    }

    public List<TrainingTypeEntity> getAll() {
        return trainingTypeRepository.getAll();
    }
}
