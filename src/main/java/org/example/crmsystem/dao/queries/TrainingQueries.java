package org.example.crmsystem.dao.queries;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TrainingQueries {
    GET_BY_NAME("""
            SELECT t FROM TrainingEntity t
            WHERE t.name
            LIKE :name
            """);

    private final String query;
}
