package org.example.crmsystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.crmsystem.dto.trainee.request.TraineeRegistrationRequestDTO;
import org.example.crmsystem.dto.trainee.request.TraineeUpdateRequestDTO;
import org.example.crmsystem.dto.trainer.TrainerServiceDTO;
import org.example.crmsystem.dto.training.TrainingAddRequestDTO;
import org.example.crmsystem.dto.user.UserUpdateStatusRequestDTO;
import org.example.crmsystem.entity.TrainingType;
import org.example.crmsystem.entity.TrainingTypeEntity;
import org.example.crmsystem.service.TrainerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TraineeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TrainerService trainerService;

    private final String username = "John.Doe";
    private TraineeRegistrationRequestDTO registrationRequest;
    private TraineeUpdateRequestDTO updateRequest;
    private UserUpdateStatusRequestDTO updateStatusRequest;
    private TrainerServiceDTO trainerDTO1;
    private TrainerServiceDTO trainerDTO2;
    private TrainingAddRequestDTO trainingDTO;

    @BeforeEach
    void setUp() throws Exception {
        registrationRequest = TraineeRegistrationRequestDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .address("Odesa")
                .build();
        updateRequest = TraineeUpdateRequestDTO.builder()
                .username(username)
                .firstName("John")
                .lastName("Brown")
                .address("Kyiv")
                .isActive(false)
                .build();

        trainerDTO1 = TrainerServiceDTO.builder()
                .specialization(new TrainingTypeEntity(1, TrainingType.FITNESS))
                .firstName("Andrew")
                .lastName("Montgomery")
                .username("Andrew.Montgomery")
                .password("1234")
                .isActive(true)
                .trainings(new ArrayList<>())
                .trainees(new ArrayList<>())
                .build();
        trainerDTO2 = TrainerServiceDTO.builder()
                .specialization(new TrainingTypeEntity(1, TrainingType.FITNESS))
                .firstName("Ben")
                .lastName("Smith")
                .username("Ben.Smith")
                .password("1234")
                .isActive(true)
                .trainings(new ArrayList<>())
                .trainees(new ArrayList<>())
                .build();
        trainingDTO = TrainingAddRequestDTO.builder()
                .traineeUsername(username)
                .trainerUsername("Andrew.Montgomery")
                .trainingName("First Yoga TrainingEntity")
                .trainingDate(LocalDateTime.of(2024, 12, 12, 12, 0, 0))
                .trainingType(new TrainingTypeEntity(1, TrainingType.FITNESS))
                .trainingDuration(120L)
                .build();

        updateStatusRequest = new UserUpdateStatusRequestDTO(true);

        mockMvc.perform(post("/trainees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)));
    }

    @AfterEach
    void tearDown() throws Exception {
        mockMvc.perform(delete("/trainees/John.Doe"));
    }

    @Test
    void testRegisterTrainee_Successful() throws Exception {
        TraineeRegistrationRequestDTO registrationRequest = TraineeRegistrationRequestDTO.builder()
                .firstName("John")
                .lastName("Bob")
                .address("Odesa")
                .build();
        mockMvc.perform(post("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("John.Bob"))
                .andExpect(jsonPath("$.password", notNullValue()))
                .andReturn();
    }

    @Test
    void testGetTrainee_Successful() throws Exception {
        mockMvc.perform(get("/trainees/John.Doe"))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.address").value("Odesa"));
    }

    @Test
    void testUpdateTrainee_Successful() throws Exception {
        mockMvc.perform(post("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        mockMvc.perform(put("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.address").value("Kyiv"))
                .andExpect(jsonPath("$.lastName").value("Brown"));
    }

    @Test
    void testDeleteTrainee_Successful() throws Exception {
        mockMvc.perform(delete("/trainees/John.Doe"))
                .andExpect(status().isOk());
    }

    @Test
    void testToggleActiveStatus_Successful() throws Exception {

        mockMvc.perform(patch("/trainees/{username}/status", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStatusRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTrainersNotAssignedToTrainee() throws Exception {
        registrationRequest.setFirstName("Katya");
        mockMvc.perform(post("/trainees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)));

        trainerDTO1.setLastName("Ross");
        trainerDTO2.setLastName("John");
        trainerDTO1 = trainerService.createProfile(trainerDTO1);
        trainerService.createProfile(trainerDTO2);

        trainingDTO.setTraineeUsername("Katya.Doe");
        trainingDTO.setTrainerUsername(trainerDTO1.getUsername());

        mockMvc.perform(post("/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trainingDTO)));

        mockMvc.perform(get("/trainees/{username}/trainers/unsigned", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Ben"))
                .andExpect(jsonPath("$[0].lastName").value("John"));
    }

    @Test
    void testUpdateTraineeTrainers() throws Exception {
        trainerService.createProfile(trainerDTO1);
        trainerService.createProfile(trainerDTO2);

        mockMvc.perform(post("/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trainingDTO)));

        mockMvc.perform(put("/trainees/{username}/trainers", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of("Ben.Smith"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("Andrew.Montgomery"))
                .andExpect(jsonPath("$[1].username").value("Ben.Smith"));
    }

    @Test
    void testGetTraineeTrainings_Successful() throws Exception {
        trainerDTO1.setFirstName("Trainer");
        trainerDTO1.setLastName("One");

        trainerDTO2.setFirstName("Trainer");
        trainerDTO2.setLastName("Two");

        trainerService.createProfile(trainerDTO1);
        trainerService.createProfile(trainerDTO2);

        trainingDTO.setTrainerUsername("Trainer.One");

        mockMvc.perform(post("/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trainingDTO)));
        mockMvc.perform(get("/trainees/{username}/trainings", username)
                        .param("fromDate", "2022-03-01T00:00:00")
                        .param("toDate", "2025-03-31T23:59:59")
                        .param("trainerUsername", "Trainer.One"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].trainingName").value("First Yoga TrainingEntity"));
    }
}
