package org.example.crmsystem.model;

import lombok.Getter;

@Getter

public enum TrainingType {
    PILATES("PILATES");
    private final String trainingTypeName;

    TrainingType(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }
}
