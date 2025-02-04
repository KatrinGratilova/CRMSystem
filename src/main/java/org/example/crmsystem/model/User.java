package org.example.crmsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User {
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private boolean isActive;
}
