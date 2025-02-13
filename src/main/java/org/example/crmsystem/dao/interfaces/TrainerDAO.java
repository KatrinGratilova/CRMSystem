package org.example.crmsystem.dao.interfaces;

import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.exception.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrainerDAO extends HavingUserName<TrainerEntity> {
    TrainerEntity add(TrainerEntity trainerEntity);

    Optional<TrainerEntity> getById(long id);

    TrainerEntity update(TrainerEntity trainerEntity) throws EntityNotFoundException;

    boolean toggleActiveStatus(TrainerEntity trainerEntity) throws EntityNotFoundException;

    List<TrainerEntity> getTrainersNotAssignedToTrainee(String traineeUserName);

    List<TrainingEntity> getTrainerTrainingsByCriteria(
            String trainerUserName, LocalDateTime fromDate, LocalDateTime toDate, String traineeName);
}
