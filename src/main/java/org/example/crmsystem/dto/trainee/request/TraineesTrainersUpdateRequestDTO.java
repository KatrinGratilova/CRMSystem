package org.example.crmsystem.dto.trainee.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode
@Data
@NoArgsConstructor
@SuperBuilder
public class TraineesTrainersUpdateRequestDTO {
    private String traineeUsername;
    private List<String> trainerUsernames;
}
