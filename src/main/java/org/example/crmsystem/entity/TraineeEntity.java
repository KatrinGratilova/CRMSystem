package org.example.crmsystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder

@Entity
@Table(name = "trainees")
@PrimaryKeyJoinColumn(name = "trainee_id")
public class TraineeEntity extends UserEntity {
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column
    private String address;

    @OneToMany(mappedBy = "trainee", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainingEntity> trainings = new ArrayList<>();

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    }, fetch = FetchType.EAGER)
    @JoinTable(name = "trainees_trainers",
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id"))
    private List<TrainerEntity> trainers = new ArrayList<>();

    public TraineeEntity(long userId, String firstName, String lastName, String userName, String password, boolean isActive, LocalDate dateOfBirth, String address) {
        super(userId, firstName, lastName, userName, password, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TraineeEntity: ").append(super.toString());
        String str =
                ", dateOfBirth=" + dateOfBirth +
                        ", address='" + address + '\'' + ", trainers=[";
        sb.append(str);

        for (TrainerEntity trainer : trainers)
            sb.append(trainer.getId()).append(" ");
        sb.append("], trainings=[");
        for (TrainingEntity training : trainings)
            sb.append(training.getId()).append(" ");
        sb.append("]");

        return sb.toString();
    }

    @PrePersist
    protected void onCreate() {
        if (trainers.isEmpty()) trainers = new ArrayList<>();
        if (trainings.isEmpty()) trainings = new ArrayList<>();
    }

    public void addTrainer(TrainerEntity trainer) {
        trainers.add(trainer);
    }
}
