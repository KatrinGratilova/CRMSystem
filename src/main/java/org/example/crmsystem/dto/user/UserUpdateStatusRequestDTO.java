package org.example.crmsystem.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserUpdateStatusRequestDTO {
    @NotNull(message = "Active status is required.")
    private Boolean isActive;
}
