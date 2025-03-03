package org.example.crmsystem.converter;

import org.example.crmsystem.dto.trainee.TraineeNestedDTO;
import org.example.crmsystem.dto.trainee.TraineeServiceDTO;
import org.example.crmsystem.dto.trainee.TraineeWithoutListsServiceDTO;
import org.example.crmsystem.dto.trainee.request.TraineeRegistrationRequestDTO;
import org.example.crmsystem.dto.trainee.request.TraineeUpdateRequestDTO;
import org.example.crmsystem.dto.trainee.response.TraineeGetResponseDTO;
import org.example.crmsystem.dto.trainee.response.TraineeUpdateResponseDTO;
import org.example.crmsystem.entity.TraineeEntity;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TraineeConverter extends UserConverter {
    public static TraineeServiceDTO toServiceDTO(TraineeRegistrationRequestDTO trainee) {
        return TraineeServiceDTO.builder()
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .trainers(new ArrayList<>())
                .trainings(new ArrayList<>())
                .build();
    }

    public static TraineeServiceDTO toServiceDTO(TraineeEntity trainee) {
        return TraineeServiceDTO.builder()
                .id(trainee.getId())
                .username(trainee.getUsername())
                .password(trainee.getPassword())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .isActive(trainee.isActive())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .trainers(trainee.getTrainers().stream().map(TrainerConverter::toWithoutListDTO).collect(Collectors.toList()))
                .trainings(trainee.getTrainings().stream().map(TrainingConverter::toWithoutUsersDTO).collect(Collectors.toList()))
                .build();
    }

    public static TraineeEntity toEntity(TraineeServiceDTO trainee) {
        return TraineeEntity.builder()
                .id(trainee.getId())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .username(trainee.getUsername())
                .password(trainee.getPassword())
                .isActive(trainee.isActive())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .trainers(trainee.getTrainers().stream().map(TrainerConverter::toEntity).collect(Collectors.toList()))
                .trainings(trainee.getTrainings().stream().map(TrainingConverter::toEntity).collect(Collectors.toList()))
                .build();
    }

    public static TraineeEntity toEntity(TraineeWithoutListsServiceDTO trainee) {
        return TraineeEntity.builder()
                .id(trainee.getId())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .username(trainee.getUsername())
                .password(trainee.getPassword())
                .isActive(trainee.isActive())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .build();
    }

    public static TraineeGetResponseDTO toGetResponseDTO(TraineeServiceDTO trainee) {
        return TraineeGetResponseDTO.builder()
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .isActive(trainee.isActive())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .trainers(trainee.getTrainers().stream().map(TrainerConverter::toNestedDTO).collect(Collectors.toList()))
                .build();
    }

    public static TraineeNestedDTO toNestedDTO(TraineeWithoutListsServiceDTO trainee) {
        return TraineeNestedDTO.builder()
                .username(trainee.getUsername())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .build();
    }

    public static TraineeUpdateResponseDTO toUpdateResponseDTO(TraineeServiceDTO trainee) {
        return TraineeUpdateResponseDTO.builder()
                .username(trainee.getUsername())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .isActive(trainee.isActive())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .trainers(trainee.getTrainers().stream().map(TrainerConverter::toNestedDTO).collect(Collectors.toList()))
                .build();
    }

    public static TraineeServiceDTO toServiceDTO(TraineeUpdateRequestDTO trainee) {
        return TraineeServiceDTO.builder()
                .username(trainee.getUsername())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .isActive(trainee.getIsActive())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .trainers(new ArrayList<>())
                .trainings(new ArrayList<>())
                .build();
    }

    public static TraineeWithoutListsServiceDTO toWithoutListDTO(TraineeEntity trainee) {
        return TraineeWithoutListsServiceDTO.builder()
                .id(trainee.getId())
                .username(trainee.getUsername())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .password(trainee.getPassword())
                .isActive(trainee.isActive())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .build();
    }

    public static TraineeWithoutListsServiceDTO toWithoutListDTO(TraineeServiceDTO trainee) {
        return TraineeWithoutListsServiceDTO.builder()
                .id(trainee.getId())
                .username(trainee.getUsername())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .password(trainee.getPassword())
                .isActive(trainee.isActive())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .build();
    }
}
