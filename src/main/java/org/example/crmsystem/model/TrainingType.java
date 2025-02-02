package org.example.crmsystem.model;

import lombok.Getter;

@Getter

public enum TrainingType {
    PILATES("PILATES"),
    FITNESS("FITNESS"),
    YOGA("YOGA"),
    ZUMBA("ZUMBA"),
    STRETCHING("STRETCHING"),
    RESISTANCE("RESISTANCE");
    private final String trainingTypeName;

    TrainingType(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }
}
