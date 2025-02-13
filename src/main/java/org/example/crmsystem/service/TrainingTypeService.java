package org.example.crmsystem.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.crmsystem.entity.TrainingType;
import org.example.crmsystem.entity.TrainingTypeEntity;
import org.example.crmsystem.dao.TrainingTypeRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class TrainingTypeService {
    private final TrainingTypeRepository trainingTypeRepository;

    @PostConstruct
    public void initTrainingTypes() {
        Arrays.stream(TrainingType.values())
                .forEach(type -> trainingTypeRepository.add(new TrainingTypeEntity(0, type)));
    }

//    public TrainingTypeEntity add(TrainingTypeEntity entity) {
//        return trainingTypeRepository.add1(entity);
//    }

    public TrainingTypeEntity getById(int id) {
        return trainingTypeRepository.getById(id);
    }
}
