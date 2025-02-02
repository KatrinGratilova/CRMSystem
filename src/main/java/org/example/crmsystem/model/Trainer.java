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
    private long trainerId;
    private TrainingType specialization;
    private List<Long> trainerTrainings = new ArrayList<>();

    public Trainer(String firstName, String lastName, String userName, String password, boolean isActive, TrainingType specialization, long trainerId) {
        super(firstName, lastName, userName, password, isActive);
        this.trainerId = trainerId;
        this.specialization = specialization;
    }
}
