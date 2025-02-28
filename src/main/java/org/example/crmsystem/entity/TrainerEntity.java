package org.example.crmsystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@SuperBuilder

@Entity
@Table(name = "trainers")
@PrimaryKeyJoinColumn(name = "trainer_id")
public class TrainerEntity extends UserEntity {
    @ManyToOne
    @JoinColumn(name = "specialization_id", nullable = false)
    private TrainingTypeEntity specialization;

    @OneToMany(mappedBy = "trainer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<TrainingEntity> trainings = new ArrayList<>();

    @ManyToMany(mappedBy = "trainers", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private List<TraineeEntity> trainees = new ArrayList<>();

    public TrainerEntity(long userId, String firstName, String lastName, String userName, String password, boolean isActive, TrainingTypeEntity specialization) {
        super(userId, firstName, lastName, userName, password, isActive);
        this.specialization = specialization;
    }

    @PrePersist
    protected void onCreate() {
        if (trainees.isEmpty()) trainees = new ArrayList<>();
        if (trainings.isEmpty()) trainings = new ArrayList<>();
    }

    public void addTrainee(TraineeEntity trainee) {
        trainees.add(trainee);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TrainerEntity: ").append(super.toString());
        String str = ", specialization=" + specialization + ", trainees=[";
        sb.append(str);

        for (TraineeEntity trainer : trainees)
            sb.append(trainer.getId()).append(" ");
        sb.append("], trainings=[");
        for (TrainingEntity training : trainings)
            sb.append(training.getId()).append(" ");
        sb.append("]");

        return sb.toString();
    }
}
