package org.example.crmsystem.dto.trainer.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.crmsystem.dto.user.UserUpdateDTO;
import org.example.crmsystem.entity.TrainingTypeEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class TrainerUpdateRequestDTO extends UserUpdateDTO {
    private TrainingTypeEntity specialization;
}