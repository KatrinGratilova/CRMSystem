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
public class UserCredentialsDTO {
    @NotBlank(message = "Username is required.")
    private String username;
    @NotBlank(message = "Password is required.")
    private String password;
}
