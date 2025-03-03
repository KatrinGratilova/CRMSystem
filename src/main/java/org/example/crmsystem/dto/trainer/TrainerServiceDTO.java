package org.example.crmsystem.dto.trainer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.crmsystem.dto.trainee.TraineeWithoutListsServiceDTO;
import org.example.crmsystem.dto.training.TrainingWithoutUsersServiceDTO;
import org.example.crmsystem.dto.user.UserServiceDTO;
import org.example.crmsystem.entity.TrainingTypeEntity;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class TrainerServiceDTO extends UserServiceDTO {
    private TrainingTypeEntity specialization;
    private List<TrainingWithoutUsersServiceDTO> trainings = new ArrayList<>();
    private List<TraineeWithoutListsServiceDTO> trainees = new ArrayList<>();
}
