package org.example.crmsystem.dao;

import org.example.crmsystem.model.Trainee;

public interface TraineeRepository {
    Trainee add(Trainee trainee);

    Trainee update(Trainee trainee);

    boolean deleteById(long id);

    Trainee getById(long id);
}
