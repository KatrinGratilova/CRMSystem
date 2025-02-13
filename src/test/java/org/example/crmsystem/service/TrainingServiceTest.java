package org.example.crmsystem.service;

import org.example.crmsystem.dao.interfaces.TrainingDAO;
import org.example.crmsystem.entity.*;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.exception.IncompatibleSpecialization;
import org.example.crmsystem.exception.UserIsNotAuthenticated;
import org.example.crmsystem.messages.ExceptionMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {
    @Mock
    private TrainingDAO trainingDAO;
    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @InjectMocks
    private TrainingService trainingService;

    private TrainingEntity trainingEntity;
    private TrainerEntity trainerEntity;

    @BeforeEach
    void setUp() {
        trainingEntity = TrainingEntity.builder()
                .id(1L)
                .trainingName("First Yoga TrainingEntity")
                .trainingDate(LocalDateTime.of(2024, 12, 12, 12, 0, 0))
                .trainingType(new TrainingTypeEntity(1, TrainingType.YOGA))
                .trainingDuration(120L)
                .build();
        trainerEntity = TrainerEntity.builder()
                .id(2L)
                .specialization(new TrainingTypeEntity(1, TrainingType.YOGA))
                .build();
    }

    @Test
    void testAddTraining_Successful() {
        when(trainingDAO.add(trainingEntity)).thenReturn(trainingEntity);

        TrainingEntity addedTrainingEntity = trainingService.add(trainingEntity);

        assertNotNull(addedTrainingEntity);
        assertEquals(1L, addedTrainingEntity.getId());
        verify(trainingDAO, times(1)).add(trainingEntity);
    }

    @Test
    void testGetTrainingById_TrainingFound_Successful() throws EntityNotFoundException {
        when(trainingDAO.getById(1L)).thenReturn(Optional.of(trainingEntity));

        TrainingEntity result = trainingService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(trainingDAO, times(1)).getById(1L);
    }

    @Test
    void testGetTrainingById_TrainingNotFound_ThrowsEntityNotFoundException() {
        when(trainingDAO.getById(100L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> trainingService.getById(100L));

        assertEquals(ExceptionMessages.TRAINING_NOT_FOUND.format(100L), exception.getMessage());
        verify(trainingDAO, times(1)).getById(100L);
    }

    @Test
    void testUpdateTraining_EntitiesFound_Successful() throws EntityNotFoundException, IncompatibleSpecialization, UserIsNotAuthenticated {
        trainingEntity.setTrainer(trainerEntity);

        when(trainingDAO.update(trainingEntity)).thenReturn(trainingEntity);
        when(trainerService.getById(2L)).thenReturn(trainerEntity);

        TrainingEntity updatedTrainingEntity = trainingService.update(trainingEntity);

        assertNotNull(updatedTrainingEntity);
        assertEquals(1L, updatedTrainingEntity.getId());

        verify(trainingDAO, times(1)).update(trainingEntity);
        verify(trainerService, times(1)).getById(2L);
    }

    @Test
    void testUpdateTraining_DifferentTrainerAndTrainingType_ThrowsIncompatibleSpecialization() throws EntityNotFoundException, UserIsNotAuthenticated {
        trainerEntity.setSpecialization(new TrainingTypeEntity(2, TrainingType.PILATES));
        trainingEntity.setTrainer(trainerEntity);

        when(trainerService.getById(2L)).thenReturn(trainerEntity);

        IncompatibleSpecialization exception = assertThrows(IncompatibleSpecialization.class, () -> trainingService.update(trainingEntity));

        assertEquals(ExceptionMessages.INCOMPATIBLE_SPECIALIZATION.format(2L, 1L), exception.getMessage());
        verify(trainingDAO, never()).update(trainingEntity);
    }
}
