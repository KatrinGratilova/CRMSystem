package org.example.crmsystem.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@SuperBuilder
public class Trainer extends User {
    private String specialization;
    private long userId;

    public Trainer(String firstName, String lastName, String userName, String password, boolean isActive, String specialization, long userId) {
        super(firstName, lastName, userName, password, isActive);
        this.specialization = specialization;
        this.userId = userId;
    }
}
