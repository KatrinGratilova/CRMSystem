package org.example.crmsystem.dao.interfaces;

import org.example.crmsystem.model.Training;

import java.util.Optional;

public interface TrainingDAO {
    Training add(Training training);

    Optional<Training> getById(long id);
}
