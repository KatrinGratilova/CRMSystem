package org.example.crmsystem.service;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityNotFoundException;
import org.example.crmsystem.converter.TraineeConverter;
import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.dao.interfaces.TraineeRepositoryCustom;
import org.example.crmsystem.dto.trainee.TraineeServiceDTO;
import org.example.crmsystem.dto.trainer.TrainerNestedDTO;
import org.example.crmsystem.dto.trainer.TrainerServiceDTO;
import org.example.crmsystem.dto.training.TrainingByTraineeDTO;
import org.example.crmsystem.dto.user.UserUpdateStatusRequestDTO;
import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.utils.PasswordGenerator;
import org.example.crmsystem.utils.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {
    @Mock
    private TraineeDAO traineeDAO;
    @Mock
    private TraineeRepositoryCustom repositoryCustom;
    @Mock
    private TrainerService trainerService;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private UsernameGenerator usernameGenerator;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private MeterRegistry meterRegistry;

    @InjectMocks
    private TraineeService traineeService;

    private TraineeEntity traineeEntity;
    private TraineeServiceDTO traineeDTO;
    private final String username = "Andrew.Montgomery";

    @BeforeEach
    void setUp() {
        traineeEntity = TraineeEntity.builder()
                .id(1L)
                .dateOfBirth(LocalDate.of(2000, 12, 12))
                .address("Odesa, st. Bunina")
                .firstName("Andrew")
                .lastName("Montgomery")
                .username("Andrew.Montgomery")
                .password("1234")
                .isActive(true)
                .trainings(new ArrayList<>())
                .trainers(new ArrayList<>())
                .build();

        traineeDTO = TraineeConverter.toServiceDTO(traineeEntity);
    }

//    @Test
//    void testCreateProfile_Successful() {
//        String generatedPassword = "password123";
//        String generatedUsername = "Andrew.Montgomery";
//
//        traineeEntity.setUsername(generatedUsername);
//        traineeEntity.setPassword(generatedPassword);
//
//        when(passwordGenerator.generateUserPassword()).thenReturn(generatedPassword);
//        when(usernameGenerator.generateUsername(traineeDTO)).thenReturn(generatedUsername);
//        when(authenticationService.authenticate(generatedUsername, generatedPassword)).thenReturn(true);
//
//        when(traineeDAO.save(any(TraineeEntity.class))).thenReturn(traineeEntity);
//
//        TraineeServiceDTO result = traineeService.createProfile(traineeDTO);
//
//        assertNotNull(result);
//        assertEquals(traineeEntity.getPassword(), result.getPassword());
//        assertEquals(traineeEntity.getUsername(), result.getUsername());
//
//        verify(traineeDAO, times(1)).save(traineeEntity);
//        verify(passwordGenerator, times(1)).generateUserPassword();
//        verify(usernameGenerator, times(1)).generateUsername(traineeDTO);
//        verify(authenticationService, times(1)).authenticate(generatedUsername, generatedPassword);
//    }

    @Test
    void testGetByUsername_TraineeFound_Successful() throws EntityNotFoundException {
        String userName = "Andrew.Montgomery";
        traineeEntity.setUsername(userName);
        when(repositoryCustom.getByUsername(userName)).thenReturn(Optional.of(traineeEntity));

        TraineeServiceDTO result = traineeService.getByUsername(userName);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(userName, result.getUsername());
        verify(repositoryCustom, times(1)).getByUsername(userName);
    }

    @Test
    void testGetByByUsername_TraineeNotFound_ThrowsEntityNotFoundException() {
        String userName = "Andrew.Montgomery";
        when(repositoryCustom.getByUsername(userName)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> traineeService.getByUsername(userName));

        assertEquals(ExceptionMessages.TRAINEE_NOT_FOUND.format(userName), exception.getMessage());
        verify(repositoryCustom, times(1)).getByUsername(userName);
    }

    @Test
    void testUpdate_TraineeFound_Successful() throws EntityNotFoundException {
        when(repositoryCustom.update(traineeEntity)).thenReturn(traineeEntity);

        TraineeServiceDTO result = traineeService.update(traineeDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repositoryCustom, times(1)).update(traineeEntity);
    }

    @Test
    void testUpdate_TraineeNotFound_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        when(repositoryCustom.update(traineeEntity)).thenThrow(new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(1L)));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> traineeService.update(traineeDTO));

        assertEquals(ExceptionMessages.TRAINEE_NOT_FOUND.format(1L), exception.getMessage());
        verify(repositoryCustom, times(1)).update(traineeEntity);
    }

    @Test
    void testDeleteByUsername_TraineeFound_Successful() {
        String userName = "Andrew.Montgomery";

        traineeService.deleteByUsername(userName);

        verify(repositoryCustom, times(1)).deleteByUsername(userName);
    }

    @Test
    void testToggleActiveStatus_TraineeFound_Successful() throws EntityNotFoundException {
        when(repositoryCustom.toggleActiveStatus(username, true)).thenReturn(true);

        boolean result = traineeService.toggleActiveStatus(username, new UserUpdateStatusRequestDTO(true));

        assertTrue(result);
        verify(repositoryCustom, times(1)).toggleActiveStatus(username, true);
    }

    @Test
    void testToggleActiveStatus_TraineeNotFound_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        when(repositoryCustom.toggleActiveStatus(username, true)).thenThrow(new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(1L)));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> traineeService.toggleActiveStatus(username, new UserUpdateStatusRequestDTO(true)));

        assertEquals(ExceptionMessages.TRAINEE_NOT_FOUND.format(1L), exception.getMessage());
        verify(repositoryCustom, times(1)).toggleActiveStatus(username, true);
    }

    @Test
    void testGetTraineeTrainingsByCriteria_Successful() {
        String userName = "Andrew.Montgomery";

        LocalDateTime fromDate = LocalDateTime.now().minusDays(30);
        LocalDateTime toDate = LocalDateTime.now();
        List<TrainingEntity> trainings = List.of(
                TrainingEntity.builder()
                        .trainer(new TrainerEntity())
                        .trainee(TraineeEntity.builder()
                                .username(userName)
                                .build())
                        .build(),
                TrainingEntity.builder()
                        .trainer(new TrainerEntity())
                        .trainee(TraineeEntity.builder()
                                .username(userName)
                                .build())
                        .build());

        when(repositoryCustom.getTraineeTrainingsByCriteria(userName, fromDate, toDate, "trainer", "type"))
                .thenReturn(trainings);

        List<TrainingByTraineeDTO> result = traineeService.getTraineeTrainingsByCriteria(userName, fromDate, toDate, "trainer", "type");

        assertEquals(2, result.size());
        verify(repositoryCustom, times(1)).getTraineeTrainingsByCriteria(userName, fromDate, toDate, "trainer", "type");
    }

    @Test
    void testGetTrainersNotAssignedToTrainee_Successful() {
        List<TrainerEntity> trainings = List.of(new TrainerEntity(), new TrainerEntity());

        when(repositoryCustom.getTrainersNotAssignedToTrainee(username)).thenReturn(trainings);

        List<TrainerNestedDTO> result = traineeService.getTrainersNotAssignedToTrainee(username);

        assertEquals(2, result.size());
        verify(repositoryCustom, times(1)).getTrainersNotAssignedToTrainee(username);
    }

    @Test
    void testUpdateTraineeTrainers_Successful() {
        when(repositoryCustom.getByUsername(username)).thenReturn(Optional.of(traineeEntity));
        when(trainerService.getByUsername("trainer1")).thenReturn(new TrainerServiceDTO());
        when(trainerService.getByUsername("trainer2")).thenReturn(new TrainerServiceDTO());

        traineeEntity.setTrainers(new ArrayList<>());
        when(repositoryCustom.updateTrainers(traineeEntity)).thenReturn(traineeEntity);

        List<TrainerNestedDTO> result = traineeService.updateTraineeTrainers(username, List.of("trainer1", "trainer2"));

        assertEquals(2, result.size());
        verify(repositoryCustom, times(1)).getByUsername(username);
        verify(trainerService, times(2)).getByUsername(anyString());
        verify(repositoryCustom, times(1)).updateTrainers(traineeEntity);
    }
}
