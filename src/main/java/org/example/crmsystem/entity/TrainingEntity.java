package org.example.crmsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

@Entity
@Table(name = "trainings")
public class TrainingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "training_id")
    private long trainingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id")
    private TraineeEntity trainee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private TrainerEntity trainer;

    @Column(name = "training_name")
    private String trainingName;

    @Column(name = "training_type")
    private TrainingType trainingType;

    @Column(name = "training_date")
    private LocalDateTime trainingDate;

    @Column(name = "training_time")
    private int trainingTime;
}
