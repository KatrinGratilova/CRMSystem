package org.example.crmsystem.dao.interfaces;

import org.example.crmsystem.model.Trainer;

import java.util.Optional;

public interface TrainerDAO {
    Trainer add(Trainer trainer);

    Trainer update(Trainer trainer);

    Optional<Trainer> getById(long id);

    void addTraining(long trainerId, long trainingId);
}
