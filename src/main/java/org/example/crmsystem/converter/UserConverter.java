package org.example.crmsystem.converter;

import org.example.crmsystem.dto.user.UserCredentialsDTO;
import org.example.crmsystem.dto.user.UserRegisterResponseDTO;
import org.example.crmsystem.dto.user.UserServiceDTO;

public class UserConverter {
    public static UserCredentialsDTO toUserCredentialsDTO(UserServiceDTO user) {
        return UserCredentialsDTO.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }

    public static UserRegisterResponseDTO toUserRegistrationResponseDTO(UserServiceDTO user) {
        return UserRegisterResponseDTO.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }
}
