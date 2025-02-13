package org.example.crmsystem.service;

import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.entity.TrainingType;
import org.example.crmsystem.entity.TrainingTypeEntity;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.exception.UserIsNotAuthenticated;
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
                .build();
    }

    @Test
    void testCreateProfile_Successful() {
        String generatedPassword = "password123";
        String generatedUsername = "Andrew.Montgomery";

        TrainerEntity addedTrainerEntity = TrainerEntity.builder()
                .id(1L)
                .firstName("Andrew")
                .lastName("Montgomery")
                .isActive(true)
                .userName(generatedUsername)
                .password(generatedPassword)
                .build();

        when(passwordGenerator.generateUserPassword()).thenReturn(generatedPassword);
        when(usernameGenerator.generateUserName(trainerEntity)).thenReturn(generatedUsername);

        when(trainerDAO.add(any(TrainerEntity.class))).thenReturn(addedTrainerEntity);

        TrainerEntity result = trainerService.createProfile(trainerEntity);

        assertNotNull(result);
        assertEquals(addedTrainerEntity.getPassword(), result.getPassword());
        assertEquals(addedTrainerEntity.getUserName(), result.getUserName());

        verify(trainerDAO, times(1)).add(trainerEntity);
        verify(passwordGenerator, times(1)).generateUserPassword();
        verify(usernameGenerator, times(1)).generateUserName(trainerEntity);
    }

    @Test
    void testGetById_TrainerFound_Successful() throws EntityNotFoundException, UserIsNotAuthenticated {
        when(trainerDAO.getById(1L)).thenReturn(Optional.of(trainerEntity));
        when(authenticationService.isAuthenticated(1L)).thenReturn(true);

        TrainerEntity result = trainerService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(trainerDAO, times(1)).getById(1L);
        verify(authenticationService, times(1)).isAuthenticated(1L);

    }

    @Test
    void testGetById_TrainerIsNotAuthenticated_ThrowsUserIsNotAuthenticated() {
        when(authenticationService.isAuthenticated(1L)).thenReturn(false);

        UserIsNotAuthenticated exception = assertThrows(UserIsNotAuthenticated.class,
                () -> trainerService.getById(1L));

        assertEquals(ExceptionMessages.USER_IS_NOT_AUTHENTICATED.format(1L), exception.getMessage());
        verify(authenticationService, times(1)).isAuthenticated(1L);
        verify(trainerDAO, never()).getById(1L);
    }

    @Test
    void testGetById_TrainerNotFound_ThrowsEntityNotFoundException() {
        when(trainerDAO.getById(100L)).thenReturn(Optional.empty());
        when(authenticationService.isAuthenticated(100L)).thenReturn(true);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> trainerService.getById(100L));

        assertEquals(ExceptionMessages.TRAINER_NOT_FOUND.format(100L), exception.getMessage());
        verify(trainerDAO, times(1)).getById(100L);
        verify(authenticationService, times(1)).isAuthenticated(100L);
    }

    @Test
    void testGetByUserName_TrainerFound_Successful() throws EntityNotFoundException, UserIsNotAuthenticated {
        String userName = "Andrew.Montgomery";
        trainerEntity.setUserName(userName);
        when(trainerDAO.getByUserName(userName)).thenReturn(Optional.of(trainerEntity));
        when(authenticationService.isAuthenticated(userName)).thenReturn(true);

        TrainerEntity result = trainerService.getByUsername(userName);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(userName, result.getUserName());
        verify(trainerDAO, times(1)).getByUserName(userName);
        verify(authenticationService, times(1)).isAuthenticated(userName);
    }

    @Test
    void testGetByByUserName_TrainerIsNotAuthenticated_ThrowsUserIsNotAuthenticated() {
        String userName = "Andrew.Montgomery";
        when(authenticationService.isAuthenticated(userName)).thenReturn(false);

        UserIsNotAuthenticated exception = assertThrows(UserIsNotAuthenticated.class,
                () -> trainerService.getByUsername(userName));

        assertEquals(ExceptionMessages.USER_IS_NOT_AUTHENTICATED_WITH_USERNAME.format(userName), exception.getMessage());
        verify(authenticationService, times(1)).isAuthenticated(userName);
        verify(trainerDAO, never()).getByUserName(anyString());
    }

    @Test
    void testGetByByUserName_TrainerNotFound_ThrowsEntityNotFoundException() {
        String userName = "Andrew.Montgomery";
        when(trainerDAO.getByUserName(userName)).thenReturn(Optional.empty());
        when(authenticationService.isAuthenticated(userName)).thenReturn(true);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> trainerService.getByUsername(userName));

        assertEquals(ExceptionMessages.TRAINER_NOT_FOUND_BY_USERNAME.format(userName), exception.getMessage());
        verify(trainerDAO, times(1)).getByUserName(userName);
        verify(authenticationService, times(1)).isAuthenticated(userName);
    }

    @Test
    void testUpdate_TrainerFound_Successful() throws EntityNotFoundException, UserIsNotAuthenticated {
        when(trainerDAO.update(trainerEntity)).thenReturn(trainerEntity);
        when(authenticationService.isAuthenticated(1L)).thenReturn(true);

        TrainerEntity result = trainerService.update(trainerEntity);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(trainerDAO, times(1)).update(trainerEntity);
        verify(authenticationService, times(1)).isAuthenticated(1L);
    }

    @Test
    void testUpdate_TrainerIsNotAuthenticated_ThrowsUserIsNotAuthenticated() throws EntityNotFoundException {
        when(authenticationService.isAuthenticated(1L)).thenReturn(false);

        UserIsNotAuthenticated exception = assertThrows(UserIsNotAuthenticated.class,
                () -> trainerService.update(trainerEntity));

        assertEquals(ExceptionMessages.USER_IS_NOT_AUTHENTICATED.format(1L), exception.getMessage());
        verify(trainerDAO, never()).update(trainerEntity);
        verify(authenticationService, times(1)).isAuthenticated(1L);
    }

    @Test
    void testUpdate_TrainerNotFound_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        when(trainerDAO.update(trainerEntity)).thenThrow(new EntityNotFoundException(ExceptionMessages.TRAINER_NOT_FOUND.format(1L)));
        when(authenticationService.isAuthenticated(1L)).thenReturn(true);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> trainerService.update(trainerEntity));

        assertEquals(ExceptionMessages.TRAINER_NOT_FOUND.format(1L), exception.getMessage());
        verify(trainerDAO, times(1)).update(trainerEntity);
        verify(authenticationService, times(1)).isAuthenticated(1L);
    }

    @Test
    void testToggleActiveStatus_TrainerFound_Successful() throws UserIsNotAuthenticated, EntityNotFoundException {
        when(authenticationService.isAuthenticated(1L)).thenReturn(true);
        when(trainerDAO.toggleActiveStatus(trainerEntity)).thenReturn(true);

        boolean result = trainerService.toggleActiveStatus(trainerEntity);

        assertTrue(result);
        verify(trainerDAO, times(1)).toggleActiveStatus(trainerEntity);
        verify(authenticationService, times(1)).isAuthenticated(1L);
    }

    @Test
    void testToggleActiveStatus_TrainerNotAuthenticated_ThrowsUserIsNotAuthenticated() throws EntityNotFoundException {
        when(authenticationService.isAuthenticated(1L)).thenReturn(false);

        UserIsNotAuthenticated exception = assertThrows(UserIsNotAuthenticated.class,
                () -> trainerService.toggleActiveStatus(trainerEntity));

        assertEquals(ExceptionMessages.USER_IS_NOT_AUTHENTICATED.format(1L), exception.getMessage());
        verify(trainerDAO, never()).toggleActiveStatus(any(TrainerEntity.class));
        verify(authenticationService, times(1)).isAuthenticated(1L);
    }

    @Test
    void testToggleActiveStatus_TrainerNotFound_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        when(trainerDAO.toggleActiveStatus(trainerEntity)).thenThrow(new EntityNotFoundException(ExceptionMessages.TRAINER_NOT_FOUND.format(1L)));
        when(authenticationService.isAuthenticated(1L)).thenReturn(true);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> trainerService.toggleActiveStatus(trainerEntity));

        assertEquals(ExceptionMessages.TRAINER_NOT_FOUND.format(1L), exception.getMessage());
        verify(trainerDAO, times(1)).toggleActiveStatus(any(TrainerEntity.class));
        verify(authenticationService, times(1)).isAuthenticated(1L);
    }

    @Test
    void testChangePassword_OldPasswordCorrect_Successful() throws EntityNotFoundException, UserIsNotAuthenticated {
        String userName = "Andrew.Montgomery";

        trainerEntity.setPassword("oldPassword");
        when(trainerDAO.getByUserName(userName)).thenReturn(Optional.of(trainerEntity));
        when(trainerDAO.update(trainerEntity)).thenReturn(trainerEntity);
        when(authenticationService.isAuthenticated(1L)).thenReturn(true);

        boolean result = trainerService.changePassword(userName, "oldPassword", "newPassword");

        assertTrue(result);
        assertEquals("newPassword", trainerEntity.getPassword());
        verify(trainerDAO, times(1)).update(trainerEntity);
        verify(authenticationService, times(1)).isAuthenticated(1L);
    }

    @Test
    void testChangePassword_OldPasswordIsNotCorrect_ReturnsFalse() throws EntityNotFoundException, UserIsNotAuthenticated {
        String userName = "Andrew.Montgomery";

        trainerEntity.setPassword("oldPassword1");
        when(trainerDAO.getByUserName(userName)).thenReturn(Optional.of(trainerEntity));
        when(authenticationService.isAuthenticated(1L)).thenReturn(true);

        boolean result = trainerService.changePassword(userName, "oldPassword", "newPassword");

        assertFalse(result);
        verify(trainerDAO, times(1)).getByUserName(userName);
        verify(authenticationService, times(1)).isAuthenticated(1L);
        verify(trainerDAO, never()).update(any(TrainerEntity.class));
    }

    @Test
    void testChangePassword_TrainerNotAuthenticated_ThrowsUserIsNotAuthenticated() throws EntityNotFoundException {
        String userName = "Andrew.Montgomery";

        when(trainerDAO.getByUserName(userName)).thenReturn(Optional.of(trainerEntity));
        when(authenticationService.isAuthenticated(1L)).thenReturn(false);

        UserIsNotAuthenticated exception = assertThrows(UserIsNotAuthenticated.class,
                () -> trainerService.changePassword(userName, "oldPassword", "newPassword"));

        assertEquals(ExceptionMessages.USER_IS_NOT_AUTHENTICATED.format(1L), exception.getMessage());
        verify(trainerDAO, never()).update(any(TrainerEntity.class));
        verify(authenticationService, times(1)).isAuthenticated(1L);
        verify(trainerDAO, times(1)).getByUserName(userName);

    }

    @Test
    void testChangePassword_TrainerNotFound_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        String userName = "Andrew.Montgomery";

        when(trainerDAO.getByUserName(userName)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> trainerService.changePassword(userName, "oldPassword", "newPassword"));

        assertEquals(ExceptionMessages.TRAINER_WITH_USERNAME_IS_NOT_FOUND.format(userName), exception.getMessage());
        verify(trainerDAO, never()).update(any(TrainerEntity.class));
        verify(trainerDAO, times(1)).getByUserName(userName);
    }

    @Test
    void testGetTrainerTrainingsByCriteria_Successful() throws UserIsNotAuthenticated {
        String userName = "Andrew.Montgomery";
        when(authenticationService.isAuthenticated(userName)).thenReturn(true);

        LocalDateTime fromDate = LocalDateTime.now().minusDays(30);
        LocalDateTime toDate = LocalDateTime.now();
        List<TrainingEntity> trainings = List.of(new TrainingEntity(), new TrainingEntity());

        when(trainerDAO.getTrainerTrainingsByCriteria(userName, fromDate, toDate, "trainee"))
                .thenReturn(trainings);

        List<TrainingEntity> result = trainerService.getTrainerTrainingsByCriteria(userName, fromDate, toDate, "trainee");

        assertEquals(2, result.size());
        verify(trainerDAO, times(1)).getTrainerTrainingsByCriteria(userName, fromDate, toDate, "trainee");
        verify(authenticationService, times(1)).isAuthenticated(userName);
    }

    @Test
    void testGetTrainerTrainingsByCriteria_UserIsNotAuthenticated_ThrowsUserIsNotAuthenticated() {
        String userName = "Andrew.Montgomery";
        when(authenticationService.isAuthenticated(userName)).thenReturn(false);

        LocalDateTime fromDate = LocalDateTime.now().minusDays(30);
        LocalDateTime toDate = LocalDateTime.now();

        UserIsNotAuthenticated exception = assertThrows(UserIsNotAuthenticated.class,
                () -> trainerService.getTrainerTrainingsByCriteria(userName, fromDate, toDate, "trainee"));

        assertEquals(exception.getMessage(), ExceptionMessages.USER_IS_NOT_AUTHENTICATED_WITH_USERNAME.format(userName));
        verify(authenticationService, times(1)).isAuthenticated(userName);
        verify(trainerDAO, never()).getTrainerTrainingsByCriteria(userName, fromDate, toDate, "trainee");
    }

    @Test
    void testGetTrainersNotAssignedToTrainee_Successful() throws UserIsNotAuthenticated {
        String userName = "Andrew.Montgomery";
        when(authenticationService.isAuthenticated(userName)).thenReturn(true);

        List<TrainerEntity> trainings = List.of(new TrainerEntity(), new TrainerEntity());

        when(trainerDAO.getTrainersNotAssignedToTrainee(userName)).thenReturn(trainings);

        List<TrainerEntity> result = trainerService.getTrainersNotAssignedToTrainee(userName);

        assertEquals(2, result.size());
        verify(trainerDAO, times(1)).getTrainersNotAssignedToTrainee(userName);
        verify(authenticationService, times(1)).isAuthenticated(userName);
    }

    @Test
    void testGetTrainersNotAssignedToTrainee_UserIsNotAuthenticated_ThrowsUserIsNotAuthenticated() {
        String userName = "Andrew.Montgomery";
        when(authenticationService.isAuthenticated(userName)).thenReturn(false);

        UserIsNotAuthenticated exception = assertThrows(UserIsNotAuthenticated.class,
                () -> trainerService.getTrainersNotAssignedToTrainee(userName));

        assertEquals(exception.getMessage(), ExceptionMessages.USER_IS_NOT_AUTHENTICATED_WITH_USERNAME.format(userName));
        verify(authenticationService, times(1)).isAuthenticated(userName);
        verify(trainerDAO, never()).getTrainersNotAssignedToTrainee(userName);
    }
}

