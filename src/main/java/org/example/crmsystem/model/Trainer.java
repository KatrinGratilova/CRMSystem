package org.example.crmsystem.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@SuperBuilder
public class Trainer extends User {
    private TrainingType specialization;
    private long trainerId;
    private List<Long> trainerTrainings;

    public Trainer(String firstName, String lastName, String userName, String password, boolean isActive, TrainingType specialization, long userId) {
        super(firstName, lastName, userName, password, isActive);
        this.specialization = specialization;
        this.trainerId = userId;
        trainerTrainings = new ArrayList<>();
    }
}
