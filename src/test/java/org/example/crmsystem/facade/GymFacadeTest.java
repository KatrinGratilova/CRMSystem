package org.example.crmsystem.facade;

import org.example.crmsystem.entity.*;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.exception.IncompatibleSpecialization;
import org.example.crmsystem.exception.UserIsNotAuthenticated;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.service.TraineeService;
import org.example.crmsystem.service.TrainerService;
import org.example.crmsystem.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GymFacadeTest {
    @InjectMocks
    private GymFacade gymFacade;
    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;

    private TraineeEntity traineeEntity;
    private TrainerEntity trainerEntity;
    private TrainingEntity trainingEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        traineeEntity = TraineeEntity.builder()
                .id(1L)
                .dateOfBirth(LocalDate.of(2000, 12, 12))
                .address("Yk")
                .firstName("Andrew")
                .lastName("Bob")
                .isActive(true)
                .build();

        trainerEntity = TrainerEntity.builder()
                .id(2L)
                .firstName("Bob")
                .lastName("Ross")
                .specialization(new TrainingTypeEntity(1, TrainingType.YOGA))
                .isActive(true)
                .build();

        trainingEntity = TrainingEntity.builder()
                .id(3L)
                .trainee(null)
                .trainer(null)
                .trainingName("First Yoga TrainingEntity")
                .trainingDate(LocalDateTime.of(2024, 12, 12, 12, 0, 0))
                .trainingType(new TrainingTypeEntity(1, TrainingType.YOGA))
                .trainingDuration(120L)
                .build();
    }

    @Test
    void testCreateTraineeProfile_Successful() {
        when(traineeService.createProfile(traineeEntity)).thenReturn(traineeEntity);

        TraineeEntity result = gymFacade.createTraineeProfile(traineeEntity);

        assertEquals(traineeEntity, result);
        verify(traineeService).createProfile(traineeEntity);
    }

    @Test
    void testCreateTrainerProfile_Successful() {
        when(trainerService.createProfile(trainerEntity)).thenReturn(trainerEntity);

        TrainerEntity result = gymFacade.createTrainerProfile(trainerEntity);

        assertEquals(trainerEntity, result);
        verify(trainerService).createProfile(trainerEntity);
    }

    @Test
    void testCreateTrainingProfile_Successful() {
        when(trainingService.add(trainingEntity)).thenReturn(trainingEntity);

        TrainingEntity result = gymFacade.createTrainingProfile(trainingEntity);

        assertEquals(trainingEntity, result);
        verify(trainingService).add(trainingEntity);
    }

    @Test
    void testPlanTraining_AllEntitiesExist_Successful() throws UserIsNotAuthenticated, EntityNotFoundException, IncompatibleSpecialization {
        long traineeId = 1L;
        long trainerId = 2L;

        gymFacade.planTraining(trainingEntity, traineeEntity, trainerEntity);

        assertEquals(traineeId, trainingEntity.getTrainee().getId());
        assertEquals(trainerId, trainingEntity.getTrainer().getId());
        verify(trainingService).update(trainingEntity);
    }

    @Test
    void testPlanTraining_shouldHandleEntityNotFoundException() throws EntityNotFoundException, UserIsNotAuthenticated, IncompatibleSpecialization {
        long trainingId = 3L;

        doThrow(new EntityNotFoundException(ExceptionMessages.TRAINING_NOT_FOUND.format(trainingId)))
                .when(trainingService).update(any(TrainingEntity.class));

        gymFacade.planTraining(trainingEntity, traineeEntity, trainerEntity);

        verify(trainingService).update(trainingEntity);
    }

    @Test
    void testPlanTraining_shouldHandleIncompatibleSpecializationException() throws EntityNotFoundException, IncompatibleSpecialization, UserIsNotAuthenticated {
        long trainingId = 3L;
        long trainerId = 2L;

        doThrow(new IncompatibleSpecialization(ExceptionMessages.INCOMPATIBLE_SPECIALIZATION.format(trainerId, trainingId)))
                .when(trainingService).update(trainingEntity);

        gymFacade.planTraining(trainingEntity, traineeEntity, trainerEntity);

        verify(trainingService).update(trainingEntity);
    }
}
