package org.example.crmsystem.service;

import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.model.Trainee;
import org.example.crmsystem.utils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {
    @Mock
    private TraineeDAO traineeDAO;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private UsernameGenerator usernameGenerator;

    @InjectMocks
    private TraineeService traineeService;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = Trainee.builder()
                .traineeId(1L)
                .dateOfBirth(LocalDate.of(2000, 12, 12))
                .address("Odesa, st. Bunina")
                .firstName("Andrew")
                .lastName("Montgomery")
                .isActive(true)
                .build();
    }

    @Test
    void testAddTrainee_Successful() {
        String generatedPassword = "password123";
        String generatedUsername = "Andrew.Montgomery";

        Trainee addedTrainee = Trainee.builder()
                .traineeId(1L)
                .dateOfBirth(LocalDate.of(2000, 12, 12))
                .address("Odesa, st. Bunina")
                .firstName("Andrew")
                .lastName("Montgomery")
                .isActive(true)
                .traineeTrainings(new ArrayList<>())
                .userName(generatedUsername)
                .password(generatedPassword)
                .build();

        when(passwordGenerator.generateUserPassword()).thenReturn(generatedPassword);
        when(usernameGenerator.generateUserName(trainee)).thenReturn(generatedUsername);

        when(traineeDAO.add(any(Trainee.class))).thenReturn(addedTrainee);

        Trainee result = traineeService.add(trainee);

        assertNotNull(result);
        assertEquals(addedTrainee.getPassword(), result.getPassword());
        assertEquals(addedTrainee.getUserName(), result.getUserName());

        verify(traineeDAO, times(1)).add(trainee);
        verify(passwordGenerator, times(1)).generateUserPassword();
        verify(usernameGenerator, times(1)).generateUserName(trainee);
    }

    @Test
    void testGetById_TraineeFound_Successful() throws EntityNotFoundException {
        when(traineeDAO.getById(1L)).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getTraineeId());
        verify(traineeDAO, times(1)).getById(1L);
    }

    @Test
    void testGetById_TraineeNotFound_ThrowsEntityNotFoundException() {
        when(traineeDAO.getById(100L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> traineeService.getById(100L));

        assertEquals("Trainee with ID 100 not found.", exception.getMessage());
        verify(traineeDAO, times(1)).getById(100L);
    }

    @Test
    void testUpdate_TraineeFound_Successful() throws EntityNotFoundException {
        when(traineeDAO.update(trainee)).thenReturn(trainee);

        Trainee result = traineeService.update(trainee);

        assertNotNull(result);
        assertEquals(1L, result.getTraineeId());
        verify(traineeDAO, times(1)).update(trainee);
    }

    @Test
    void testUpdate_TraineeNotFound_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        when(traineeDAO.update(trainee)).thenThrow(new EntityNotFoundException("Trainee with ID 1 not found."));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> traineeService.update(trainee));

        assertEquals("Trainee with ID 1 not found.", exception.getMessage());
        verify(traineeDAO, times(1)).update(trainee);
    }

    @Test
    void testDeleteById_TraineeFound_True() {
        when(traineeDAO.deleteById(1L)).thenReturn(true);

        boolean result = traineeService.deleteById(1L);

        assertTrue(result);
        verify(traineeDAO, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteById_TraineeNotFound_False() {
        when(traineeDAO.deleteById(1L)).thenReturn(false);

        boolean result = traineeService.deleteById(1L);

        assertFalse(result);
        verify(traineeDAO, times(1)).deleteById(1L);
    }

    @Test
    void testAddTraining_TraineeFound_Successful() throws EntityNotFoundException {
        doNothing().when(traineeDAO).addTraining(1L, 101L);

        traineeService.addTraining(1L, 101L);

        verify(traineeDAO, times(1)).addTraining(1L, 101L);
    }

    @Test
    void testAddTraining_TraineeNotFound_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        doThrow(new EntityNotFoundException("Trainee with ID 1 does not exist."))
                .when(traineeDAO).addTraining(1L, 101L);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> traineeService.addTraining(1L, 101L));

        assertEquals("Trainee with ID 1 does not exist.", exception.getMessage());
        verify(traineeDAO, times(1)).addTraining(1L, 101L);
    }
}
