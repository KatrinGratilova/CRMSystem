package org.example.crmsystem.converter;

import org.example.crmsystem.dto.trainer.TrainerNestedDTO;
import org.example.crmsystem.dto.trainer.TrainerServiceDTO;
import org.example.crmsystem.dto.trainer.request.TrainerRegistrationRequestDTO;
import org.example.crmsystem.dto.trainer.request.TrainerUpdateRequestDTO;
import org.example.crmsystem.dto.trainer.response.TrainerGetResponseDTO;
import org.example.crmsystem.dto.trainer.response.TrainerUpdateResponseDTO;
import org.example.crmsystem.entity.TrainerEntity;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TrainerConverter extends UserConverter {
    public static TrainerServiceDTO toServiceDTO(TrainerRegistrationRequestDTO trainer) {
        return TrainerServiceDTO.builder()
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .specialization(trainer.getSpecialization())
                .trainees(new ArrayList<>())
                .trainings(new ArrayList<>())
                .build();
    }

    public static TrainerServiceDTO toServiceDTO(TrainerEntity trainer) {
        return TrainerServiceDTO.builder()
                .id(trainer.getId())
                .userName(trainer.getUserName())
                .password(trainer.getPassword())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .isActive(trainer.isActive())
                .specialization(trainer.getSpecialization())
                .trainees(new ArrayList<>())
                .trainings(new ArrayList<>())
                //.trainees(trainer.getTrainees().stream().map(TraineeConverter::toServiceDTO).collect(Collectors.toList()))
                //.trainings(trainer.getTrainings().stream().map(TrainingConverter::toServiceDTO).collect(Collectors.toList()))
                .build();
    }

    public static TrainerEntity toEntity(TrainerServiceDTO trainer) {
        return TrainerEntity.builder()
                .id(trainer.getId())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .userName(trainer.getUserName())
                .isActive(trainer.isActive())
                .specialization(trainer.getSpecialization())
                .password(trainer.getPassword())
                .trainees(trainer.getTrainees().stream().map(TraineeConverter::toEntity).collect(Collectors.toList()))
                .trainings(trainer.getTrainings().stream().map(TrainingConverter::toEntity).collect(Collectors.toList()))
                .build();
    }

    public static TrainerGetResponseDTO toGetResponseDTO(TrainerServiceDTO trainer) {
        return TrainerGetResponseDTO.builder()
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .isActive(trainer.isActive())
                .specialization(trainer.getSpecialization())
                .trainees(trainer.getTrainees().stream().map(TraineeConverter::toNestedDTO).collect(Collectors.toList()))
                .build();
    }

    public static TrainerNestedDTO toNestedDTO(TrainerServiceDTO trainer) {
        return TrainerNestedDTO.builder()
                .username(trainer.getUserName())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .specialization(trainer.getSpecialization())
                .build();
    }

    public static TrainerUpdateResponseDTO toUpdateResponseDTO(TrainerServiceDTO trainer) {
        return TrainerUpdateResponseDTO.builder()
                .username(trainer.getUserName())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .isActive(trainer.isActive())
                .specialization(trainer.getSpecialization())
                .trainees(trainer.getTrainees().stream().map(TraineeConverter::toGetResponseDTO).collect(Collectors.toList()))
                .build();
    }

    public static TrainerServiceDTO toServiceDTO(TrainerUpdateRequestDTO trainer) {
        return TrainerServiceDTO.builder()
                .userName(trainer.getUsername())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .isActive(trainer.isActive())
                .specialization(trainer.getSpecialization())
                .trainees(new ArrayList<>())
                .trainings(new ArrayList<>())
                .build();
    }
}
