package org.example.crmsystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class Trainee extends User {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private String address;
    private long traineeId;
    private List<Long> traineeTrainings;

    public Trainee(String firstName, String lastName, String userName, String password, boolean isActive, LocalDate dateOfBirth, String address, long userId) {
        super(firstName, lastName, userName, password, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.traineeId = userId;
        traineeTrainings = new ArrayList<>();
    }
}
