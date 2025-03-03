package org.example.crmsystem.dto.trainee;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.crmsystem.dto.user.UserServiceDTO;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class TraineeWithoutListsServiceDTO extends UserServiceDTO {
    private LocalDate dateOfBirth;
    private String address;
}
