package org.example.crmsystem.converter;

import org.example.crmsystem.dto.user.UserServiceDTO;
import org.example.crmsystem.dto.user.UserCredentialsDTO;

public class UserConverter {
    public static UserCredentialsDTO toRegistrationResponseDTO(UserServiceDTO user) {
        return UserCredentialsDTO.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }
}
