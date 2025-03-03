package org.example.crmsystem.dto.training;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String trainingName;
    @NotBlank
    private LocalDateTime trainingDate;
    @NotBlank
    private TrainingTypeEntity trainingType;
    @NotBlank
    private long trainingDuration;
    @NotBlank
    private String trainerUsername;
}
