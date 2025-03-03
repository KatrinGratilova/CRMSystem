package org.example.crmsystem.dto.trainer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.crmsystem.dto.user.UserServiceDTO;
import org.example.crmsystem.entity.TrainingTypeEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class TrainerWithoutListsServiceDTO extends UserServiceDTO {
    private TrainingTypeEntity specialization;
}
