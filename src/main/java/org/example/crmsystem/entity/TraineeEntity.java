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
public class TraineeEntity extends UserEntity {
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column
    private String address;

    @OneToMany(mappedBy = "trainee")
    private List<TrainingEntity> trainings = new ArrayList<>();

    @ManyToMany
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
        String str = super.toString();
        return str + " TraineeEntity{" +
                "dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                ", trainings=" + trainings +
                ", trainers=" + trainers +
                '}';
    }
}
