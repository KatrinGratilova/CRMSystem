package org.example.crmsystem.dao.interfaces;

import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeDAO {
    Trainee add(Trainee trainee);

    Optional<Trainee> getById(long id);

    Trainee update(Trainee trainee) throws EntityNotFoundException;

    boolean deleteById(long id);

    List<Trainee> getByUserName(String userName);

    void addTraining(long traineeId, long trainingId) throws EntityNotFoundException;
}
