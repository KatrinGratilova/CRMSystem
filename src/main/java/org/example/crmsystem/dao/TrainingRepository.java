package org.example.crmsystem.dao;

import org.example.crmsystem.model.Trainee;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingRepository {
    Trainee add(Trainee trainee);

    Trainee getById(long id);
}
