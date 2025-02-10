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
public class TrainerEntity extends UserEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "trainer_id")
//    private long trainerId;

    @Column(name = "specialization")
    private TrainingType specialization;

    @OneToMany(mappedBy = "trainer")
    private List<TrainingEntity> trainings = new ArrayList<>();

    @ManyToMany(mappedBy = "trainers")
    private List<TraineeEntity> trainees = new ArrayList<>();

    public TrainerEntity(long userId, String firstName, String lastName, String userName, String password, boolean isActive, TrainingType specialization) {
        super(userId, firstName, lastName, userName, password, isActive);
        //this.trainerId = trainerId;
        this.specialization = specialization;
    }
}
