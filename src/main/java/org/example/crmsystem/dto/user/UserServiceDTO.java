package org.example.crmsystem.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserServiceDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private boolean isActive;
}
