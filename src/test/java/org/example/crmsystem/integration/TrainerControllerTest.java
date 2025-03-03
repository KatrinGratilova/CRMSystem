package org.example.crmsystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.crmsystem.dto.trainee.TraineeServiceDTO;
import org.example.crmsystem.dto.trainer.request.TrainerRegistrationRequestDTO;
import org.example.crmsystem.dto.trainer.request.TrainerUpdateRequestDTO;
import org.example.crmsystem.dto.training.TrainingAddRequestDTO;
import org.example.crmsystem.dto.user.UserUpdateStatusRequestDTO;
import org.example.crmsystem.entity.TrainingType;
import org.example.crmsystem.entity.TrainingTypeEntity;
import org.example.crmsystem.service.TraineeService;
import org.example.crmsystem.utils.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class TrainerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String username = "John.Doe";
    private TrainerRegistrationRequestDTO registrationRequest;
    private TrainerUpdateRequestDTO updateRequest;
    private UserUpdateStatusRequestDTO updateStatusRequest;

    TraineeServiceDTO traineeDTO1 = TraineeServiceDTO.builder()
            .firstName("Andrew")
            .lastName("Montgomery")
            .userName("Andrew.Montgomery")
            .password("1234")
            .isActive(true)
            .trainings(new ArrayList<>())
            .trainers(new ArrayList<>())
            .build();
    TrainingAddRequestDTO trainingDTO = TrainingAddRequestDTO.builder()
            .trainerUsername(username)
            .traineeUsername("Andrew.Montgomery")
            .trainingName("First Yoga TrainingEntity")
            .trainingDate(LocalDateTime.of(2024, 12, 12, 12, 0, 0))
            .trainingType(new TrainingTypeEntity(1, TrainingType.FITNESS))
            .trainingDuration(120L)
            .build();

    @Autowired
    private UsernameGenerator usernameGenerator;
    @Autowired
    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        registrationRequest = TrainerRegistrationRequestDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .specialization(new TrainingTypeEntity(1, TrainingType.FITNESS))
                .build();
        updateRequest = TrainerUpdateRequestDTO.builder()
                .username(username)
                .firstName("John")
                .lastName("Brown")
                .isActive(false)
                .build();

        updateStatusRequest = new UserUpdateStatusRequestDTO(true);
    }

    @Test
    void testRegisterTrainer_Successful() throws Exception {
        mockMvc.perform(post("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(username + "1"))
                .andExpect(jsonPath("$.password", notNullValue()))
                .andReturn();
    }

    @Test
    void testGetTrainer_Successful() throws Exception {
        mockMvc.perform(post("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)));
        mockMvc.perform(get("/trainers/John.Doe"))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void testUpdateTrainer_Successful() throws Exception {

        mockMvc.perform(put("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.lastName").value( "Brown"));
    }

    @Test
    void testToggleActiveStatus_Successful() throws Exception {
        mockMvc.perform(patch("/trainers/{username}/status", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStatusRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTrainerTrainings() throws Exception {
        traineeService.createProfile(traineeDTO1);

        mockMvc.perform(post("/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trainingDTO)));
        mockMvc.perform(get("/trainees/{username}/trainings", username)
                        .param("fromDate", "2022-03-01T00:00:00")
                        .param("toDate", "2025-03-31T23:59:59")
                        .param("traineeUsername", "Andrew.Montgomery"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].trainingName").value("First Yoga TrainingEntity"));
    }
}
