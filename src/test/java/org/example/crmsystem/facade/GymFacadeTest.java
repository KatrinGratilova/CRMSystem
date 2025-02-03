package org.example.crmsystem.facade;

import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.exception.IncompatibleSpecialization;
import org.example.crmsystem.model.*;
import org.example.crmsystem.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GymFacadeTest {

    @InjectMocks
    private GymFacade gymFacade;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        trainee = Trainee.builder()
                .traineeId(1L)
                .dateOfBirth(LocalDate.of(2000, 12, 12))
                .address("Yk")
                .firstName("Andrew")
                .lastName("Bob")
                .isActive(true)
                .build();

        trainer = Trainer.builder()
                .trainerId(2L)
                .firstName("Bob")
                .lastName("Ross")
                .specialization(TrainingType.YOGA)
                .isActive(true)
                .build();

        training = Training.builder()
                .trainingId(3L)
                .traineeId(1L)
                .trainerId(2L)
                .trainingName("First Yoga Training")
                .trainingDate(LocalDateTime.of(2024, 12, 12, 12, 0, 0))
                .trainingType(TrainingType.YOGA)
                .trainingTime(120)
                .build();
    }

    @Test
    void testCreateTraineeProfile_Successful() {
        when(traineeService.add(trainee)).thenReturn(trainee);

        Trainee result = gymFacade.createTraineeProfile(trainee);

        assertEquals(trainee, result);
        verify(traineeService).add(trainee);
    }

    @Test
    void testCreateTrainerProfile_Successful() {
        when(trainerService.add(trainer)).thenReturn(trainer);

        Trainer result = gymFacade.createTrainerProfile(trainer);

        assertEquals(trainer, result);
        verify(trainerService).add(trainer);
    }

    @Test
    void testCreateTrainingProfile_Successful() {
        when(trainingService.add(training)).thenReturn(training);

        Training result = gymFacade.createTrainingProfile(training);

        assertEquals(training, result);
        verify(trainingService).add(training);
    }

    @Test
    void testPlanTraining_AllEntitiesExist_Successful() throws EntityNotFoundException, IncompatibleSpecialization {
        long trainingId = 3L;
        long traineeId = 1L;
        long trainerId = 2L;

        when(trainingService.getById(trainingId)).thenReturn(training);

        gymFacade.planTraining(trainingId, traineeId, trainerId);

        assertEquals(traineeId, training.getTraineeId());
        assertEquals(trainerId, training.getTrainerId());
        verify(trainingService).update(training);
        verify(traineeService).addTraining(traineeId, trainingId);
        verify(trainerService).addTraining(trainerId, trainingId);
    }

    @Test
    void testPlanTraining_shouldHandleEntityNotFoundException() throws EntityNotFoundException {
        long trainingId = 3L;
        long traineeId = 1L;
        long trainerId = 2L;

        when(trainingService.getById(trainingId)).thenThrow(new EntityNotFoundException("Training not found"));

        gymFacade.planTraining(trainingId, traineeId, trainerId);

        verify(trainingService).getById(trainingId);
        verifyNoInteractions(traineeService);
        verifyNoInteractions(trainerService);
    }

    @Test
    void testPlanTraining_shouldHandleIncompatibleSpecializationException() throws EntityNotFoundException, IncompatibleSpecialization {
        long trainingId = 3L;
        long traineeId = 1L;
        long trainerId = 2L;

        when(trainingService.getById(trainingId)).thenReturn(training);
        doThrow(new IncompatibleSpecialization("Specialization mismatch"))
                .when(trainingService).update(training);

        gymFacade.planTraining(trainingId, traineeId, trainerId);

        verify(trainingService).update(training);
        verifyNoInteractions(traineeService);
        verifyNoInteractions(trainerService);
    }
}
