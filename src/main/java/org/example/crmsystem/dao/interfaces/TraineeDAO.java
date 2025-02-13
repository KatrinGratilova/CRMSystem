package org.example.crmsystem.dao.interfaces;

import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.exception.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TraineeDAO extends HavingUserName<TraineeEntity> {
    TraineeEntity add(TraineeEntity traineeEntity);

    Optional<TraineeEntity> getById(long id);

    TraineeEntity update(TraineeEntity traineeEntity) throws EntityNotFoundException;

    void deleteByUserName(String userName);

    boolean delete(TraineeEntity traineeEntity);

    boolean toggleActiveStatus(TraineeEntity traineeEntity) throws EntityNotFoundException;

    List<TrainingEntity> getTraineeTrainingsByCriteria(
            String traineeUserName, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType);
}
