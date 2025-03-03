package org.example.crmsystem.dao.interfaces;

import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.exception.IncompatibleSpecialization;

import java.util.List;
import java.util.Optional;

public interface TrainingDAO {
    TrainingEntity create(TrainingEntity trainingEntity) throws IncompatibleSpecialization;

    Optional<TrainingEntity> getById(long id);

    List<TrainingEntity> getByName(String name);
}
