package org.example.crmsystem.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TrainingType {
    PILATES("PILATES");
    private final String trainingTypeName;
}
