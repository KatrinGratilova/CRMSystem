package org.example.crmsystem.dao.interfaces;

import org.example.crmsystem.model.Trainee;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TraineeRepository {
    Trainee add(Trainee trainee) throws IOException;

    Trainee update(Trainee trainee);

    boolean deleteById(long id);

    Optional<Trainee> getById(long id);

    List<Trainee> getByUserName(String userName);

    Map<Long, Trainee> getAll();
}
