package org.example.crmsystem.dto.trainee;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode
@Data
@NoArgsConstructor
@SuperBuilder
public class TraineeNestedDTO {
    private String username;
    private String firstName;
    private String lastName;
}
