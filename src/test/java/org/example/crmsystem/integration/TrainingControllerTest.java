package org.example.crmsystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.crmsystem.dto.trainee.request.TraineeRegistrationRequestDTO;
import org.example.crmsystem.dto.trainer.request.TrainerRegistrationRequestDTO;
import org.example.crmsystem.dto.training.TrainingAddRequestDTO;
import org.example.crmsystem.dto.user.UserRegistrationRequestDTO;
import org.example.crmsystem.entity.TrainingType;
import org.example.crmsystem.entity.TrainingTypeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class TrainingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRegistrationRequestDTO traineeRequest;
    private UserRegistrationRequestDTO trainerRequest;
    private TrainingAddRequestDTO trainingRequest;
    private TrainingAddRequestDTO invalidTrainingRequest;

    @BeforeEach
    void setUp() {
        traineeRequest = TraineeRegistrationRequestDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .address("Odesa")
                .build();

        trainerRequest = TrainerRegistrationRequestDTO.builder()
                .firstName("Jane")
                .lastName("Smith")
                .specialization(new TrainingTypeEntity(1, TrainingType.FITNESS))
                .build();

        trainingRequest = TrainingAddRequestDTO.builder()
                .traineeUsername("John.Doe")
                .trainerUsername("Jane.Smith")
                .trainingType(new TrainingTypeEntity(1, TrainingType.FITNESS))
                .trainingName("First training")
                .trainingDate(LocalDateTime.of(2025, 3, 10, 10, 0))
                .trainingDuration(200)
                .build();

        invalidTrainingRequest = TrainingAddRequestDTO.builder().build();
    }

    @Test
    void testAddTraining_Successful() throws Exception {
        mockMvc.perform(post("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testAddTraining_Fails_ValidationError() throws Exception {
        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTrainingRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddTraining_Fails_UserNotFound() throws Exception {
        trainingRequest.setTraineeUsername("John.Doe100");
        trainingRequest.setTrainerUsername("Jane.Smith100");

        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.isEmptyString())));
    }
}
