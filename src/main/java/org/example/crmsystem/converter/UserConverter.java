package org.example.crmsystem.converter;

import org.example.crmsystem.dto.user.UserRegisterResponseDTO;
import org.example.crmsystem.dto.user.UserServiceDTO;

public class UserConverter {
    public static UserRegisterResponseDTO toUserRegistrationResponseDTO(UserServiceDTO user) {
        return UserRegisterResponseDTO.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }
}
