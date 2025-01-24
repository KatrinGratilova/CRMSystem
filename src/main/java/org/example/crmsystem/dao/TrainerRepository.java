package org.example.crmsystem.dao;

import org.example.crmsystem.model.Trainee;

public interface TrainerRepository {
    Trainee add(Trainee trainee);

    Trainee update(Trainee trainee);

    Trainee getById(long id);
}
