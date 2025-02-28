package org.example.crmsystem.dao.interfaces;

import org.example.crmsystem.entity.TrainingEntity;

import java.util.List;
import java.util.Optional;

public interface TrainingDAO {
    TrainingEntity add(TrainingEntity trainingEntity);

    Optional<TrainingEntity> getById(long id);

    List<TrainingEntity> getByName(String name);

//    TrainingEntity update(TrainingEntity trainingEntity) throws EntityNotFoundException;
}
