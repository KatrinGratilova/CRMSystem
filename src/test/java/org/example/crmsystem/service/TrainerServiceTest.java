package org.example.crmsystem.service;

import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.model.Trainer;
import org.example.crmsystem.model.TrainingType;
import org.example.crmsystem.utils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceTest {
    @Mock
    private TrainerDAO trainerDAO;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private UsernameGenerator usernameGenerator;

    @InjectMocks
    private TrainerService trainerService;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainer = Trainer.builder()
                .trainerId(1L)
                .specialization(TrainingType.FITNESS)
                .firstName("Andrew")
                .lastName("Montgomery")
                .isActive(true)
                .build();
    }

    @Test
    void testAddTrainer_Successful() {
        String generatedPassword = "password123";
        String generatedUsername = "Andrew.Montgomery";

        Trainer addedTrainer = Trainer.builder()
                .trainerId(1L)
                .specialization(TrainingType.FITNESS)
                .firstName("Andrew")
                .lastName("Montgomery")
                .isActive(true)
                .trainerTrainings(new ArrayList<>())
                .userName(generatedUsername)
                .password(generatedPassword)
                .build();

        when(passwordGenerator.generateUserPassword()).thenReturn(generatedPassword);
        when(usernameGenerator.generateUserName(trainer)).thenReturn(generatedUsername);

        when(trainerDAO.add(any(Trainer.class))).thenReturn(addedTrainer);

        Trainer result = trainerService.add(trainer);

        assertNotNull(result);
        assertEquals(addedTrainer.getPassword(), result.getPassword());
        assertEquals(addedTrainer.getUserName(), result.getUserName());

        verify(trainerDAO, times(1)).add(trainer);
        verify(passwordGenerator, times(1)).generateUserPassword();
        verify(usernameGenerator, times(1)).generateUserName(trainer);
    }

    @Test
    void testGetById_TrainerFound_Successful() throws EntityNotFoundException {
        when(trainerDAO.getById(1L)).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getTrainerId());
        verify(trainerDAO, times(1)).getById(1L);
    }

    @Test
    void testGetById_TrainerNotFound_ThrowsEntityNotFoundException() {
        when(trainerDAO.getById(100L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> trainerService.getById(100L));

        assertEquals("Trainer with ID 100 not found.", exception.getMessage());
        verify(trainerDAO, times(1)).getById(100L);
    }

    @Test
    void testUpdate_TrainerFound_Successfully() throws EntityNotFoundException {
        when(trainerDAO.update(trainer)).thenReturn(trainer);

        Trainer result = trainerService.update(trainer);

        assertNotNull(result);
        assertEquals(1L, result.getTrainerId());
        verify(trainerDAO, times(1)).update(trainer);
    }

    @Test
    void testUpdate_TrainerNotFound_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        when(trainerDAO.update(trainer)).thenThrow(new EntityNotFoundException("Trainer with ID 1 is not found"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> trainerService.update(trainer));

        assertEquals("Trainer with ID 1 is not found", exception.getMessage());
        verify(trainerDAO, times(1)).update(trainer);
    }

    @Test
    void testAddTraining_TrainerFound_Successfully() throws EntityNotFoundException {
        doNothing().when(trainerDAO).addTraining(1L, 101L);

        trainerService.addTraining(1L, 101L);

        verify(trainerDAO, times(1)).addTraining(1L, 101L);
    }

    @Test
    void testAddTraining_TrainerNotFound_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        doThrow(new EntityNotFoundException("Trainer with ID 1 does not exist."))
                .when(trainerDAO).addTraining(1L, 101L);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> trainerService.addTraining(1L, 101L));

        assertEquals("Trainer with ID 1 does not exist.", exception.getMessage());
        verify(trainerDAO, times(1)).addTraining(1L, 101L);
    }
}

