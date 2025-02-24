package org.example.crmsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@AllArgsConstructor
@NoArgsConstructor
@Data

@Entity
@Table(name = "training_types")
@Immutable
public class TrainingTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "training_type_id")
    private long id;

    @Enumerated(EnumType.STRING)
    private TrainingType trainingType;
}
