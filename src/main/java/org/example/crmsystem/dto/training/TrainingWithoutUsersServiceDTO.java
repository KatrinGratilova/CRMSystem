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
public class TrainingWithoutUsersServiceDTO {
    private long id;
    private long traineeId;
    private long trainerId;
    private String trainingName;
    private TrainingTypeEntity trainingType;
    private LocalDateTime trainingDate;
    private long trainingDuration;
}