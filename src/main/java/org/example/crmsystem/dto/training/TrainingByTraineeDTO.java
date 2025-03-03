package org.example.crmsystem.dto.training;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.crmsystem.entity.TrainingTypeEntity;

import java.time.LocalDateTime;

@EqualsAndHashCode
@Data
@NoArgsConstructor
@SuperBuilder
public class TrainingByTraineeDTO {
    private String trainingName;
    private LocalDateTime trainingDate;
    private TrainingTypeEntity trainingType;
    private long trainingDuration;
    private String trainerUsername;
}
