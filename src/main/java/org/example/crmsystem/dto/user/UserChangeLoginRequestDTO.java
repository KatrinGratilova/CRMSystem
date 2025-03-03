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
public class UserChangeLoginRequestDTO {
    @NotBlank(message = "Username is required.")
    private String username;
    @NotBlank(message = "Old password is required.")
    private String oldPassword;
    @NotBlank(message = "New password is required.")
    private String newPassword;
}
