package org.example.crmsystem.model;

import lombok.*;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Trainee extends User{
    private LocalDate dateOfBirth;
    private String address;
}
