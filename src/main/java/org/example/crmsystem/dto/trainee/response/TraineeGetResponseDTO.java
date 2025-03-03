package org.example.crmsystem.dto.trainee.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.crmsystem.dto.trainer.TrainerNestedDTO;
import org.example.crmsystem.dto.user.UserGetResponseDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class TraineeGetResponseDTO extends UserGetResponseDTO {
    private LocalDate dateOfBirth;
    private String address;
    private List<TrainerNestedDTO> trainers = new ArrayList<>();
}

