package org.example.crmsystem.dto.trainer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.crmsystem.entity.TrainingType;
import org.example.crmsystem.entity.TrainingTypeEntity;

@EqualsAndHashCode
@Data
@NoArgsConstructor
@SuperBuilder
public class TrainerNestedDTO{
    private String username;
    private String firstName;
    private String lastName;
    private TrainingTypeEntity specialization;
}