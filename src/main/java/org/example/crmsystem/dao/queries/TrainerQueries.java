package org.example.crmsystem.dao.queries;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TrainerQueries {
    GET_WHERE_USERNAME_STARTS_WITH("""
            SELECT t FROM TrainerEntity t
            WHERE t.userName
            LIKE :userName
            """),
    GET_BY_USERNAME("""
            SELECT t FROM TrainerEntity t
            WHERE t.userName
            LIKE :userName
            """);

    private final String query;
}
