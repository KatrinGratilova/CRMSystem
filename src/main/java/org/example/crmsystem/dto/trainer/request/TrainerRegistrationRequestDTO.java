package org.example.crmsystem.dto.trainer.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.crmsystem.dto.user.UserRegistrationRequestDTO;
import org.example.crmsystem.entity.TrainingTypeEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class TrainerRegistrationRequestDTO extends UserRegistrationRequestDTO {
    @NotNull(message = "Specialization is required.")
    private TrainingTypeEntity specialization;
}
