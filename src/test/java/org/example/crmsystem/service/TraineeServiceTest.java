package org.example.crmsystem.service;

import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.TrainingEntity;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {
    @Mock
    private TraineeDAO traineeDAO;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private UsernameGenerator usernameGenerator;
    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private TraineeService traineeService;

    private TraineeEntity traineeEntity;

    @BeforeEach
    void setUp() {
        traineeEntity = TraineeEntity.builder()
                .id(1L)
                .dateOfBirth(LocalDate.of(2000, 12, 12))
                .address("Odesa, st. Bunina")
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

        TraineeEntity addedTraineeEntity = TraineeEntity.builder()
                .id(1L)
                .dateOfBirth(LocalDate.of(2000, 12, 12))
                .address("Odesa, st. Bunina")
                .firstName("Andrew")
                .lastName("Montgomery")
                .isActive(true)
                .userName(generatedUsername)
                .password(generatedPassword)
                .build();

        when(passwordGenerator.generateUserPassword()).thenReturn(generatedPassword);
        when(usernameGenerator.generateUserName(traineeEntity)).thenReturn(generatedUsername);

        when(traineeDAO.add(any(TraineeEntity.class))).thenReturn(addedTraineeEntity);

        TraineeEntity result = traineeService.createProfile(traineeEntity);

        assertNotNull(result);
        assertEquals(addedTraineeEntity.getPassword(), result.getPassword());
        assertEquals(addedTraineeEntity.getUserName(), result.getUserName());

        verify(traineeDAO, times(1)).add(traineeEntity);
        verify(passwordGenerator, times(1)).generateUserPassword();
        verify(usernameGenerator, times(1)).generateUserName(traineeEntity);
    }

    @Test
    void testGetById_TraineeFound_Successful() throws EntityNotFoundException, UserIsNotAuthenticated {
        when(traineeDAO.getById(1L)).thenReturn(Optional.of(traineeEntity));
        when(authenticationService.isAuthenticated(1L)).thenReturn(true);

        TraineeEntity result = traineeService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(traineeDAO, times(1)).getById(1L);
        verify(authenticationService, times(1)).isAuthenticated(1L);

    }

    @Test
    void testGetById_TraineeIsNotAuthenticated_ThrowsUserIsNotAuthenticated() {
        when(authenticationService.isAuthenticated(1L)).thenReturn(false);

        UserIsNotAuthenticated exception = assertThrows(UserIsNotAuthenticated.class,
                () -> traineeService.getById(1L));

        assertEquals(ExceptionMessages.USER_IS_NOT_AUTHENTICATED.format(1L), exception.getMessage());
        verify(authenticationService, times(1)).isAuthenticated(1L);
        verify(traineeDAO, never()).getById(1L);
    }

    @Test
    void testGetById_TraineeNotFound_ThrowsEntityNotFoundException() {
        when(traineeDAO.getById(100L)).thenReturn(Optional.empty());
        when(authenticationService.isAuthenticated(100L)).thenReturn(true);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> traineeService.getById(100L));

        assertEquals(ExceptionMessages.TRAINEE_NOT_FOUND.format(100L), exception.getMessage());
        verify(traineeDAO, times(1)).getById(100L);
        verify(authenticationService, times(1)).isAuthenticated(100L);
    }

    @Test
    void testGetByUserName_TraineeFound_Successful() throws EntityNotFoundException, UserIsNotAuthenticated {
        String userName = "Andrew.Montgomery";
        traineeEntity.setUserName(userName);
        when(traineeDAO.getByUserName(userName)).thenReturn(Optional.of(traineeEntity));
        when(authenticationService.isAuthenticated(userName)).thenReturn(true);

        TraineeEntity result = traineeService.getByUsername(userName);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(userName, result.getUserName());
        verify(traineeDAO, times(1)).getByUserName(userName);
        verify(authenticationService, times(1)).isAuthenticated(userName);
    }

    @Test
    void testGetByByUserName_TraineeIsNotAuthenticated_ThrowsUserIsNotAuthenticated() {
        String userName = "Andrew.Montgomery";
        when(authenticationService.isAuthenticated(userName)).thenReturn(false);

        UserIsNotAuthenticated exception = assertThrows(UserIsNotAuthenticated.class,
                () -> traineeService.getByUsername(userName));

        assertEquals(ExceptionMessages.USER_IS_NOT_AUTHENTICATED_WITH_USERNAME.format(userName), exception.getMessage());
        verify(authenticationService, times(1)).isAuthenticated(userName);
        verify(traineeDAO, never()).getByUserName(anyString());
    }

    @Test
    void testGetByByUserName_TraineeNotFound_ThrowsEntityNotFoundException() {
        String userName = "Andrew.Montgomery";
        when(traineeDAO.getByUserName(userName)).thenReturn(Optional.empty());
        when(authenticationService.isAuthenticated(userName)).thenReturn(true);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> traineeService.getByUsername(userName));

        assertEquals(ExceptionMessages.TRAINEE_NOT_FOUND_BY_USERNAME.format(userName), exception.getMessage());
        verify(traineeDAO, times(1)).getByUserName(userName);
        verify(authenticationService, times(1)).isAuthenticated(userName);
    }

    @Test
    void testUpdate_TraineeFound_Successful() throws EntityNotFoundException, UserIsNotAuthenticated {
        when(traineeDAO.update(traineeEntity)).thenReturn(traineeEntity);
        when(authenticationService.isAuthenticated(1L)).thenReturn(true);

        TraineeEntity result = traineeService.update(traineeEntity);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(traineeDAO, times(1)).update(traineeEntity);
        verify(authenticationService, times(1)).isAuthenticated(1L);
    }

    @Test
    void testUpdate_TraineeIsNotAuthenticated_ThrowsUserIsNotAuthenticated() throws EntityNotFoundException {
        when(authenticationService.isAuthenticated(1L)).thenReturn(false);

        UserIsNotAuthenticated exception = assertThrows(UserIsNotAuthenticated.class,
                () -> traineeService.update(traineeEntity));

        assertEquals(ExceptionMessages.USER_IS_NOT_AUTHENTICATED.format(1L), exception.getMessage());
        verify(traineeDAO, never()).update(traineeEntity);
        verify(authenticationService, times(1)).isAuthenticated(1L);
    }

    @Test
    void testUpdate_TraineeNotFound_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        when(traineeDAO.update(traineeEntity)).thenThrow(new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(1L)));
        when(authenticationService.isAuthenticated(1L)).thenReturn(true);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> traineeService.update(traineeEntity));

        assertEquals(ExceptionMessages.TRAINEE_NOT_FOUND.format(1L), exception.getMessage());
        verify(traineeDAO, times(1)).update(traineeEntity);
        verify(authenticationService, times(1)).isAuthenticated(1L);
    }

    @Test
    void testDeleteByUsername_TraineeFound_Successful() throws UserIsNotAuthenticated {
        String userName = "Andrew.Montgomery";

        when(authenticationService.isAuthenticated(userName)).thenReturn(true);

        traineeService.deleteByUsername(userName);

        verify(traineeDAO, times(1)).deleteByUserName(userName);
        verify(authenticationService, times(1)).isAuthenticated(userName);
    }

    @Test
    void testDeleteByUsername_TraineeNotAuthenticated_ThrowsUserIsNotAuthenticated() {
        String userName = "Andrew.Montgomery";
        when(authenticationService.isAuthenticated(userName)).thenReturn(false);

        UserIsNotAuthenticated exception = assertThrows(UserIsNotAuthenticated.class,
                () -> traineeService.deleteByUsername(userName));

        assertEquals(ExceptionMessages.USER_IS_NOT_AUTHENTICATED_WITH_USERNAME.format(userName), exception.getMessage());
        verify(traineeDAO, never()).deleteByUserName(anyString());
        verify(authenticationService, times(1)).isAuthenticated(userName);
    }

    @Test
    void testDelete_TraineeFound_Successful() throws UserIsNotAuthenticated {
        when(authenticationService.isAuthenticated(1L)).thenReturn(true);
        when(traineeDAO.delete(traineeEntity)).thenReturn(true);

        boolean result = traineeService.delete(traineeEntity);

        assertTrue(result);
        verify(traineeDAO, times(1)).delete(traineeEntity);
        verify(authenticationService, times(1)).isAuthenticated(1L);
    }

    @Test
    void testDelete_TraineeNotAuthenticated_ThrowsUserIsNotAuthenticated() {
        when(authenticationService.isAuthenticated(1L)).thenReturn(false);

        UserIsNotAuthenticated exception = assertThrows(UserIsNotAuthenticated.class,
                () -> traineeService.delete(traineeEntity));

        assertEquals(ExceptionMessages.USER_IS_NOT_AUTHENTICATED.format(1L), exception.getMessage());
        verify(traineeDAO, never()).delete(any(TraineeEntity.class));
        verify(authenticationService, times(1)).isAuthenticated(1L);
    }

    @Test
    void testDelete_TraineeNotFound_ReturnsFalse() throws UserIsNotAuthenticated {
        when(authenticationService.isAuthenticated(1L)).thenReturn(true);
        when(traineeDAO.delete(traineeEntity)).thenReturn(false);

        boolean result = traineeService.delete(traineeEntity);

        assertFalse(result);
        verify(traineeDAO, times(1)).delete(traineeEntity);
        verify(authenticationService, times(1)).isAuthenticated(1L);
    }

    @Test
    void testToggleActiveStatus_TraineeFound_Successful() throws UserIsNotAuthenticated, EntityNotFoundException {
        when(authenticationService.isAuthenticated(1L)).thenReturn(true);
        when(traineeDAO.toggleActiveStatus(traineeEntity)).thenReturn(true);

        boolean result = traineeService.toggleActiveStatus(traineeEntity);

        assertTrue(result);
        verify(traineeDAO, times(1)).toggleActiveStatus(traineeEntity);
        verify(authenticationService, times(1)).isAuthenticated(1L);
    }

    @Test
    void testToggleActiveStatus_TraineeNotAuthenticated_ThrowsUserIsNotAuthenticated() throws EntityNotFoundException {
        when(authenticationService.isAuthenticated(1L)).thenReturn(false);

        UserIsNotAuthenticated exception = assertThrows(UserIsNotAuthenticated.class,
                () -> traineeService.toggleActiveStatus(traineeEntity));

        assertEquals(ExceptionMessages.USER_IS_NOT_AUTHENTICATED.format(1L), exception.getMessage());
        verify(traineeDAO, never()).toggleActiveStatus(any(TraineeEntity.class));
        verify(authenticationService, times(1)).isAuthenticated(1L);
    }

    @Test
    void testToggleActiveStatus_TraineeNotFound_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        when(traineeDAO.toggleActiveStatus(traineeEntity)).thenThrow(new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(1L)));
        when(authenticationService.isAuthenticated(1L)).thenReturn(true);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> traineeService.toggleActiveStatus(traineeEntity));

        assertEquals(ExceptionMessages.TRAINEE_NOT_FOUND.format(1L), exception.getMessage());
        verify(traineeDAO, times(1)).toggleActiveStatus(any(TraineeEntity.class));
        verify(authenticationService, times(1)).isAuthenticated(1L);
    }

    @Test
    void testChangePassword_OldPasswordCorrect_Successful() throws EntityNotFoundException, UserIsNotAuthenticated {
        String userName = "Andrew.Montgomery";

        traineeEntity.setPassword("oldPassword");
        when(traineeDAO.getByUserName(userName)).thenReturn(Optional.of(traineeEntity));
        when(traineeDAO.update(traineeEntity)).thenReturn(traineeEntity);
        when(authenticationService.isAuthenticated(1L)).thenReturn(true);

        boolean result = traineeService.changePassword(userName, "oldPassword", "newPassword");

        assertTrue(result);
        assertEquals("newPassword", traineeEntity.getPassword());
        verify(traineeDAO, times(1)).update(traineeEntity);
        verify(authenticationService, times(1)).isAuthenticated(1L);
    }

    @Test
    void testChangePassword_OldPasswordIsNotCorrect_ReturnsFalse() throws EntityNotFoundException, UserIsNotAuthenticated {
        String userName = "Andrew.Montgomery";

        traineeEntity.setPassword("oldPassword1");
        when(traineeDAO.getByUserName(userName)).thenReturn(Optional.of(traineeEntity));
        when(authenticationService.isAuthenticated(1L)).thenReturn(true);

        boolean result = traineeService.changePassword(userName, "oldPassword", "newPassword");

        assertFalse(result);
        verify(traineeDAO, times(1)).getByUserName(userName);
        verify(authenticationService, times(1)).isAuthenticated(1L);
        verify(traineeDAO, never()).update(any(TraineeEntity.class));
    }

    @Test
    void testChangePassword_TraineeNotAuthenticated_ThrowsUserIsNotAuthenticated() throws EntityNotFoundException {
        String userName = "Andrew.Montgomery";

        when(traineeDAO.getByUserName(userName)).thenReturn(Optional.of(traineeEntity));
        when(authenticationService.isAuthenticated(1L)).thenReturn(false);

        UserIsNotAuthenticated exception = assertThrows(UserIsNotAuthenticated.class,
                () -> traineeService.changePassword(userName, "oldPassword", "newPassword"));

        assertEquals(ExceptionMessages.USER_IS_NOT_AUTHENTICATED.format(1L), exception.getMessage());
        verify(traineeDAO, never()).update(any(TraineeEntity.class));
        verify(authenticationService, times(1)).isAuthenticated(1L);
        verify(traineeDAO, times(1)).getByUserName(userName);

    }

    @Test
    void testChangePassword_TraineeNotFound_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        String userName = "Andrew.Montgomery";

        when(traineeDAO.getByUserName(userName)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> traineeService.changePassword(userName, "oldPassword", "newPassword"));

        assertEquals(ExceptionMessages.TRAINEE_WITH_USERNAME_IS_NOT_FOUND.format(userName), exception.getMessage());
        verify(traineeDAO, never()).update(any(TraineeEntity.class));
        verify(traineeDAO, times(1)).getByUserName(userName);
    }

    @Test
    void testGetTraineeTrainingsByCriteria_Successful() throws UserIsNotAuthenticated {
        String userName = "Andrew.Montgomery";
        when(authenticationService.isAuthenticated(userName)).thenReturn(true);

        LocalDateTime fromDate = LocalDateTime.now().minusDays(30);
        LocalDateTime toDate = LocalDateTime.now();
        List<TrainingEntity> trainings = List.of(new TrainingEntity(), new TrainingEntity());

        when(traineeDAO.getTraineeTrainingsByCriteria(userName, fromDate, toDate, "trainer", "type"))
                .thenReturn(trainings);

        List<TrainingEntity> result = traineeService.getTraineeTrainingsByCriteria(userName, fromDate, toDate, "trainer", "type");

        assertEquals(2, result.size());
        verify(traineeDAO, times(1)).getTraineeTrainingsByCriteria(userName, fromDate, toDate, "trainer", "type");
        verify(authenticationService, times(1)).isAuthenticated(userName);
    }

    @Test
    void testGetTraineeTrainingsByCriteria_UserIsNotAuthenticated_ThrowsUserIsNotAuthenticated() {
        String userName = "Andrew.Montgomery";
        when(authenticationService.isAuthenticated(userName)).thenReturn(false);

        LocalDateTime fromDate = LocalDateTime.now().minusDays(30);
        LocalDateTime toDate = LocalDateTime.now();

        UserIsNotAuthenticated exception = assertThrows(UserIsNotAuthenticated.class,
                () -> traineeService.getTraineeTrainingsByCriteria(userName, fromDate, toDate, "trainer", "type"));

        assertEquals(exception.getMessage(), ExceptionMessages.USER_IS_NOT_AUTHENTICATED_WITH_USERNAME.format(userName));
        verify(authenticationService, times(1)).isAuthenticated(userName);
        verify(traineeDAO, never()).getTraineeTrainingsByCriteria(userName, fromDate, toDate, "trainer", "type");
    }
}
