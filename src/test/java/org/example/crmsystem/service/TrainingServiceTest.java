package org.example.crmsystem.service;

import org.example.crmsystem.converter.TrainingConverter;
import org.example.crmsystem.dao.interfaces.TrainingDAO;
import org.example.crmsystem.dto.training.TrainingServiceDTO;
import org.example.crmsystem.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {
    @Mock
    private TrainingDAO trainingDAO;
    @Mock
    private TrainerService trainerService;
    @InjectMocks
    private TrainingService trainingService;

    private TrainingEntity trainingEntity;

    @BeforeEach
    void setUp() {
        trainingEntity = TrainingEntity.builder()
                .id(1L)
                .trainee(TraineeEntity.builder().id(1L).build())
                .trainer(TrainerEntity.builder().id(1L).build())
                .trainingName("First Yoga TrainingEntity")
                .trainingDate(LocalDateTime.of(2024, 12, 12, 12, 0, 0))
                .trainingType(new TrainingTypeEntity(1, TrainingType.FITNESS))
                .trainingDuration(120L)
                .build();
    }

    @Test
    void testAddTraining_Successful() {
        when(trainingDAO.add(trainingEntity)).thenReturn(trainingEntity);

        TrainingServiceDTO addedTrainingDTO = trainingService.add(TrainingConverter.toServiceDTO(trainingEntity));

        assertNotNull(addedTrainingDTO);
        assertEquals(1L, addedTrainingDTO.getId());
        verify(trainingDAO, times(1)).add(trainingEntity);
    }
}
