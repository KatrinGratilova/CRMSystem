package org.example.crmsystem.dao.interfaces;

import org.example.crmsystem.model.Training;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainingRepository {
    Training add(Training training);

    Optional<Training> getById(long id);
}
