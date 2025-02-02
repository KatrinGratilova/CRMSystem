package org.example.crmsystem.dao.interfaces;

import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerDAO {
    Trainer add(Trainer trainer);

    Optional<Trainer> getById(long id);

    Trainer update(Trainer trainer) throws EntityNotFoundException;

    List<Trainer> getByUserName(String userName);

    void addTraining(long trainerId, long trainingId) throws EntityNotFoundException;
}
