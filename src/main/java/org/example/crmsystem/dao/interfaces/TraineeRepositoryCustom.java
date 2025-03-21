package org.example.crmsystem.dao.interfaces;

import jakarta.persistence.EntityNotFoundException;
import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.TrainingEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TraineeRepositoryCustom extends HavingUserName<TraineeEntity> {
    Optional<TraineeEntity> getById(long id);

    TraineeEntity update(TraineeEntity traineeEntity) throws EntityNotFoundException;

    TraineeEntity updateTrainers(TraineeEntity traineeModified) throws EntityNotFoundException;

    void updatePassword(TraineeEntity traineeEntity) throws EntityNotFoundException;

    void deleteByUsername(String userName);

    void delete(TraineeEntity traineeEntity);

    boolean toggleActiveStatus(String username, boolean isActive) throws EntityNotFoundException;

    List<TrainerEntity> getTrainersNotAssignedToTrainee(String traineeUserName);

    List<TrainingEntity> getTraineeTrainingsByCriteria(
            String traineeUserName, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType);
}
