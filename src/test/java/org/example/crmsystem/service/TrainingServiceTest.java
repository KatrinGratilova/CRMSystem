package org.example.crmsystem.service;

import org.example.crmsystem.dao.interfaces.TrainingDAO;
import org.example.crmsystem.exception.*;
import org.example.crmsystem.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

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

    private Training training;
    private Trainee trainee;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        training = Training.builder()
                .trainingId(1L)
                .trainingName("First Yoga Training")
                .trainingDate(LocalDateTime.of(2024, 12, 12, 12, 0, 0))
                .trainingType(TrainingType.YOGA)
                .trainingTime(120)
                .build();
        trainee = Trainee.builder()
                .traineeId(1L)
                .build();
        trainer = Trainer.builder()
                .trainerId(2L)
                .specialization(TrainingType.YOGA)
                .build();
    }

    @Test
    void testAddTraining_Successful() {
        when(trainingDAO.add(training)).thenReturn(training);

        Training addedTraining = trainingService.add(training);

        assertNotNull(addedTraining);
        assertEquals(1L, addedTraining.getTrainingId());
        verify(trainingDAO, times(1)).add(training);
    }

    @Test
    void testGetTrainingById_TrainingFound_Successful() throws EntityNotFoundException {
        when(trainingDAO.getById(1L)).thenReturn(Optional.of(training));

        Training result = trainingService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getTrainingId());
        verify(trainingDAO, times(1)).getById(1L);
    }

    @Test
    void testGetTrainingById_TrainingNotFound_ThrowsEntityNotFoundException() {
        when(trainingDAO.getById(100L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> trainingService.getById(100L));

        assertEquals("Training with ID 100 not found.", exception.getMessage());
        verify(trainingDAO, times(1)).getById(100L);
    }

    @Test
    void testUpdateTraining_EntitiesFound_Successful() throws EntityNotFoundException, IncompatibleSpecialization {
        training.setTraineeId(1L);
        training.setTrainerId(2L);

        when(traineeService.getById(1L)).thenReturn(trainee);
        when(trainerService.getById(2L)).thenReturn(trainer);
        when(trainingDAO.update(training)).thenReturn(training);

        Training updatedTraining = trainingService.update(training);

        assertNotNull(updatedTraining);
        assertEquals(1L, updatedTraining.getTrainingId());
        assertEquals(1L, updatedTraining.getTraineeId());
        assertEquals(2L, updatedTraining.getTrainerId());

        verify(traineeService, times(1)).getById(1L);
        verify(trainerService, times(2)).getById(2L);
        verify(trainingDAO, times(1)).update(training);
    }

    @Test
    void testUpdateTraining_TraineeNotFound_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        training.setTraineeId(1L);
        training.setTrainerId(2L);

        when(traineeService.getById(1L)).thenThrow(new EntityNotFoundException("Trainee with ID 1 not found"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> trainingService.update(training));

        assertEquals("Cannot update training: trainee or trainer with such ID is not found.", exception.getMessage());
        verify(traineeService, times(1)).getById(1L);
        verify(trainingDAO, never()).update(training);
    }

    @Test
    void testUpdateTraining_TrainerNotFound_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        training.setTraineeId(1L);
        training.setTrainerId(2L);

        when(trainerService.getById(2L)).thenThrow(new EntityNotFoundException("Trainer with ID 2 not found"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> trainingService.update(training));

        assertEquals("Cannot update training: trainee or trainer with such ID is not found.", exception.getMessage());
        verify(trainerService, times(1)).getById(2L);
        verify(trainingDAO, never()).update(training);
    }

    @Test
    void testUpdateTraining_TrainingNotFound_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        training.setTraineeId(1L);
        training.setTrainerId(2L);

        when(trainingDAO.update(training)).thenThrow(new EntityNotFoundException("Training with ID 1 does not exist."));
        when(trainerService.getById(2L)).thenReturn(trainer);


        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> trainingService.update(training));

        assertEquals("Training with ID 1 does not exist.", exception.getMessage());
        verify(trainingDAO, times(1)).update(training);
    }

    @Test
    void testUpdateTraining_DifferentTrainerAndTrainingType_ThrowsIncompatibleSpecialization() throws EntityNotFoundException {
        training.setTraineeId(1L);
        training.setTrainerId(2L);

        when(traineeService.getById(1L)).thenReturn(trainee);
        trainer.setSpecialization(TrainingType.PILATES);
        when(trainerService.getById(2L)).thenReturn(trainer);

        IncompatibleSpecialization exception = assertThrows(IncompatibleSpecialization.class, () -> trainingService.update(training));

        assertEquals("Incompatible specialization for trainer with ID 2 while adding training with ID 1.", exception.getMessage());
        verify(trainingDAO, never()).update(training);
    }
}
