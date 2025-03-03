package org.example.crmsystem.dto.training;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.crmsystem.dto.trainee.TraineeWithoutListsServiceDTO;
import org.example.crmsystem.dto.trainer.TrainerWithoutListsServiceDTO;
import org.example.crmsystem.entity.TrainingTypeEntity;

import java.time.LocalDateTime;

@EqualsAndHashCode
@Data
@NoArgsConstructor
@SuperBuilder
public class TrainingServiceDTO {
    private long id;
    private TraineeWithoutListsServiceDTO trainee;
    private TrainerWithoutListsServiceDTO trainer;
    private String trainingName;
    private TrainingTypeEntity trainingType;
    private LocalDateTime trainingDate;
    private long trainingDuration;
}
