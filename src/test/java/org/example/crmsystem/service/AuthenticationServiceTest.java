package org.example.crmsystem.service;

import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.UserEntity;
import org.example.crmsystem.exception.UserIsNotAuthenticated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private TraineeDAO traineeDAO;
    @Mock
    private TrainerDAO trainerDAO;
    @InjectMocks
    private AuthenticationService authenticationService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(traineeDAO, trainerDAO);

        user = new UserEntity();
        user.setId(1L);
        user.setUserName("testUser");
        user.setPassword("password123");
    }


    @Test
    void isAuthenticated_ValidUserName_ReturnsTrue() throws UserIsNotAuthenticated {
        when(traineeDAO.getByUserName("testUser")).thenReturn(Optional.of((TraineeEntity) user));

        authenticationService.authenticate("testUser", "password123");
        assertTrue(authenticationService.isAuthenticated("testUser"));
    }

    @Test
    void isAuthenticated_InvalidUserName_ReturnsFalse() throws UserIsNotAuthenticated {
        assertFalse(authenticationService.isAuthenticated("nonExistentUser"));
    }
}
