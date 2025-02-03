package org.example.crmsystem.dao.interfaces;

import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.model.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingDAO {
    Training add(Training training);

    Optional<Training> getById(long id);

    Training update(Training training) throws EntityNotFoundException;

    List<Training> getByName(String name);
}
