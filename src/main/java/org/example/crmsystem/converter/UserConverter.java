package org.example.crmsystem.converter;

import org.example.crmsystem.dto.user.UserServiceDTO;
import org.example.crmsystem.dto.user.UserRegistrationResponseDTO;

public class UserConverter {
    public static UserRegistrationResponseDTO toRegistrationResponseDTO(UserServiceDTO user){
        return UserRegistrationResponseDTO.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .build();
    }
}
