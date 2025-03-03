package org.example.crmsystem.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserRegistrationRequestDTO {
    @NotBlank(message = "First name is required.")
    private String firstName;
    @NotBlank(message = "Last name is required.")
    private String lastName;
}
