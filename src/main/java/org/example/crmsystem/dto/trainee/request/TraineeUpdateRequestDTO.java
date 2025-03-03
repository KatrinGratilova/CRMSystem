package org.example.crmsystem.dto.trainee.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.crmsystem.dto.user.UserUpdateDTO;

import java.time.LocalDate;
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class TraineeUpdateRequestDTO extends UserUpdateDTO {
    private LocalDate dateOfBirth;
    private String address;
}
