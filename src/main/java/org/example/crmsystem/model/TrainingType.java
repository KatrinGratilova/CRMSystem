package org.example.crmsystem.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TrainingType {
    PILATES("PILATES"),
    FITNESS("FITNESS"),
    YOGA("YOGA"),
    ZUMBA("ZUMBA"),
    STRETCHING("STRETCHING"),
    RESISTANCE("RESISTANCE");

    private final String trainingTypeName;
}
