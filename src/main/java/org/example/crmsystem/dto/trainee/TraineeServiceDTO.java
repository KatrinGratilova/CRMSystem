package org.example.crmsystem.dto.trainee;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.crmsystem.dto.trainer.TrainerWithoutListsServiceDTO;
import org.example.crmsystem.dto.training.TrainingWithoutUsersServiceDTO;
import org.example.crmsystem.dto.user.UserServiceDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class TraineeServiceDTO extends UserServiceDTO {
    private LocalDate dateOfBirth;
    private String address;
    private List<TrainingWithoutUsersServiceDTO> trainings = new ArrayList<>();
    private List<TrainerWithoutListsServiceDTO> trainers = new ArrayList<>();
}


