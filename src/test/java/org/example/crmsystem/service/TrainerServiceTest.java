package org.example.crmsystem.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.crmsystem.converter.TrainerConverter;
import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.dto.trainer.TrainerServiceDTO;
import org.example.crmsystem.dto.training.TrainingByTrainerDTO;
import org.example.crmsystem.dto.user.UserUpdateStatusRequestDTO;
import org.example.crmsystem.entity.*;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.utils.PasswordGenerator;
import org.example.crmsystem.utils.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceTest {
    @Mock
    private TrainerDAO trainerDAO;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private UsernameGenerator usernameGenerator;
    @Mock
    private AuthenticationService authenticationService;
    @InjectMocks
    private TrainerService trainerService;

    private TrainerEntity trainerEntity;
    private TrainerServiceDTO trainerDTO;
    private final String username = "Andrew.Montgomery";

    @BeforeEach
    void setUp() {
        trainerEntity = TrainerEntity.builder()
                .id(1L)
                .specialization(new TrainingTypeEntity(1, TrainingType.FITNESS))
                .firstName("Andrew")
                .lastName("Montgomery")
                .userName("Andrew.Montgomery")
                .password("1234")
                .isActive(true)
                .trainings(new ArrayList<>())
                .trainees(new ArrayList<>())
                .build();
        trainerDTO = TrainerConverter.toServiceDTO(trainerEntity);
    }

    @Test
    void testCreateProfile_Successful() {
        String generatedPassword = "password123";
        String generatedUsername = "Andrew.Montgomery";

        trainerEntity.setUserName(generatedUsername);
        trainerEntity.setPassword(generatedPassword);

        when(passwordGenerator.generateUserPassword()).thenReturn(generatedPassword);
        when(usernameGenerator.generateUserName(trainerDTO)).thenReturn(generatedUsername);
        when(authenticationService.authenticate(generatedUsername, generatedPassword)).thenReturn(true);

        when(trainerDAO.add(any(TrainerEntity.class))).thenReturn(trainerEntity);

        TrainerServiceDTO result = trainerService.createProfile(trainerDTO);

        assertNotNull(result);
        assertEquals(trainerEntity.getPassword(), result.getPassword());
        assertEquals(trainerEntity.getUserName(), result.getUserName());

        verify(trainerDAO, times(1)).add(trainerEntity);
        verify(passwordGenerator, times(1)).generateUserPassword();
        verify(usernameGenerator, times(1)).generateUserName(trainerDTO);
        verify(authenticationService, times(1)).authenticate(generatedUsername, generatedPassword);
    }

    @Test
    void testGetByUserName_TrainerFound_Successful() throws EntityNotFoundException {
        String userName = "Andrew.Montgomery";
        trainerEntity.setUserName(userName);
        when(trainerDAO.getByUserName(userName)).thenReturn(Optional.of(trainerEntity));


        TrainerServiceDTO result = trainerService.getByUsername(userName);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(userName, result.getUserName());
        verify(trainerDAO, times(1)).getByUserName(userName);
    }


    @Test
    void testGetByByUserName_TrainerNotFound_ThrowsEntityNotFoundException() {
        String userName = "Andrew.Montgomery";
        when(trainerDAO.getByUserName(userName)).thenReturn(Optional.empty());


        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> trainerService.getByUsername(userName));

        assertEquals(ExceptionMessages.TRAINER_NOT_FOUND_BY_USERNAME.format(userName), exception.getMessage());
        verify(trainerDAO, times(1)).getByUserName(userName);
    }

    @Test
    void testUpdate_TrainerFound_Successful() throws EntityNotFoundException {
        when(trainerDAO.update(trainerEntity)).thenReturn(trainerEntity);


        TrainerServiceDTO result = trainerService.update(trainerDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(trainerDAO, times(1)).update(trainerEntity);
    }

    @Test
    void testUpdate_TrainerNotFound_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        when(trainerDAO.update(trainerEntity)).thenThrow(new EntityNotFoundException(ExceptionMessages.TRAINER_NOT_FOUND.format(0, username)));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> trainerService.update(trainerDTO));

        assertEquals(ExceptionMessages.TRAINER_NOT_FOUND.format(0, username), exception.getMessage());
        verify(trainerDAO, times(1)).update(trainerEntity);
    }

    @Test
    void testToggleActiveStatus_TrainerFound_Successful() throws EntityNotFoundException {
        when(trainerDAO.toggleActiveStatus(username, true)).thenReturn(true);

        boolean result = trainerService.toggleActiveStatus(username, new UserUpdateStatusRequestDTO(true));

        assertTrue(result);
        verify(trainerDAO, times(1)).toggleActiveStatus(username, true);
    }


    @Test
    void testToggleActiveStatus_TrainerNotFound_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        when(trainerDAO.toggleActiveStatus(username, true)).thenThrow(new EntityNotFoundException(ExceptionMessages.TRAINER_NOT_FOUND.format(0, username)));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> trainerService.toggleActiveStatus(username, new UserUpdateStatusRequestDTO(true)));

        assertEquals(ExceptionMessages.TRAINER_NOT_FOUND.format(0, username), exception.getMessage());
        verify(trainerDAO, times(1)).toggleActiveStatus(username, true);
    }

    @Test
    void testGetTrainerTrainingsByCriteria_Successful() {
        LocalDateTime fromDate = LocalDateTime.now().minusDays(30);
        LocalDateTime toDate = LocalDateTime.now();
        List<TrainingEntity> trainings = List.of(
                TrainingEntity.builder()
                        .trainee(new TraineeEntity())
                        .trainer(TrainerEntity.builder()
                                .userName(username)
                                .build())
                        .build(),
                TrainingEntity.builder()
                        .trainee(new TraineeEntity())
                        .trainer(TrainerEntity.builder()
                                .userName(username).build())
                        .build());

        when(trainerDAO.getTrainerTrainingsByCriteria(username, fromDate, toDate, "trainee"))
                .thenReturn(trainings);

        List<TrainingByTrainerDTO> result = trainerService.getTrainerTrainingsByCriteria(username, fromDate, toDate, "trainee");

        assertEquals(2, result.size());
        verify(trainerDAO, times(1)).getTrainerTrainingsByCriteria(username, fromDate, toDate, "trainee");
    }
}

