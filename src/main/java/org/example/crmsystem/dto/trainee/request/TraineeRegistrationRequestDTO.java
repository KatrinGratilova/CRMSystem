package org.example.crmsystem.dto.trainee.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.crmsystem.dto.user.UserRegistrationRequestDTO;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class TraineeRegistrationRequestDTO extends UserRegistrationRequestDTO {
    private LocalDate dateOfBirth;
    private String address;
}
