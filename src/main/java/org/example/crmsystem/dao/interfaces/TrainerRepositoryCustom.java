package org.example.crmsystem.dao.interfaces;

import jakarta.persistence.EntityNotFoundException;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.TrainingEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrainerRepositoryCustom extends HavingUserName<TrainerEntity> {
    Optional<TrainerEntity> getById(long id);

    TrainerEntity update(TrainerEntity trainerEntity) throws EntityNotFoundException;

    void updatePassword(TrainerEntity trainerEntity) throws EntityNotFoundException;

    boolean toggleActiveStatus(String username, boolean isActive) throws EntityNotFoundException;

    List<TrainingEntity> getTrainerTrainingsByCriteria(
            String trainerUserName, LocalDateTime fromDate, LocalDateTime toDate, String traineeName);
}
