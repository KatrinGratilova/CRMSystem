package org.example.crmsystem.dto.trainer.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.crmsystem.dto.trainee.TraineeNestedDTO;
import org.example.crmsystem.dto.user.UserGetResponseDTO;
import org.example.crmsystem.entity.TrainingTypeEntity;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class TrainerGetResponseDTO extends UserGetResponseDTO {
    private TrainingTypeEntity specialization;
    private List<TraineeNestedDTO> trainees = new ArrayList<>();
}

