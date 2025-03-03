package org.example.crmsystem.dto.training;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class TrainingAddRequestDTO {
    @NotBlank(message = "Trainee username is required.")
    private String traineeUsername;
    @NotBlank(message = "Trainer username is required.")
    private String trainerUsername;
    @NotBlank(message = "Training name is required.")
    private String trainingName;
    @NotNull(message = "Training type is required.")
    private TrainingTypeEntity trainingType;
    @NotNull(message = "Training date is required.")
    private LocalDateTime trainingDate;
    @NotNull(message = "Training duration is required.")
    private long trainingDuration;
}
