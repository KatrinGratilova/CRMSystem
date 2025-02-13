package org.example.crmsystem.service;

import org.example.crmsystem.dao.interfaces.HavingUserName;
import org.example.crmsystem.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private HavingUserName<UserEntity> userRepository;
    @InjectMocks
    private AuthenticationService authenticationService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(List.of(userRepository));

        user = new UserEntity();
        user.setId(1L);
        user.setUserName("testUser");
        user.setPassword("password123");
    }

    @Test
    void authenticate_ValidCredentials_ReturnsTrue() {
        when(userRepository.getByUserName("testUser")).thenReturn(Optional.of(user));

        boolean result = authenticationService.authenticate("testUser", "password123");

        assertTrue(result);
        assertTrue(authenticationService.isAuthenticated(1L));
        verify(userRepository, times(1)).getByUserName("testUser");
    }

    @Test
    void authenticate_InvalidPassword_ReturnsFalse() {
        when(userRepository.getByUserName("testUser")).thenReturn(Optional.of(user));

        boolean result = authenticationService.authenticate("testUser", "wrongPassword");

        assertFalse(result);
        assertFalse(authenticationService.isAuthenticated(1L));
        verify(userRepository, times(1)).getByUserName("testUser");
    }

    @Test
    void authenticate_NonExistentUser_ReturnsFalse() {
        when(userRepository.getByUserName("nonExistentUser")).thenReturn(Optional.empty());

        boolean result = authenticationService.authenticate("nonExistentUser", "password123");

        assertFalse(result);
        assertFalse(authenticationService.isAuthenticated(999L));
        verify(userRepository, times(1)).getByUserName("nonExistentUser");
    }

    @Test
    void isAuthenticated_ValidUserId_ReturnsTrue() {
        when(userRepository.getByUserName("testUser")).thenReturn(Optional.of(user));

        authenticationService.authenticate("testUser", "password123");
        assertTrue(authenticationService.isAuthenticated(1L));
    }

    @Test
    void isAuthenticated_InvalidUserId_ReturnsFalse() {
        assertFalse(authenticationService.isAuthenticated(999L));
    }


    @Test
    void isAuthenticated_ValidUserName_ReturnsTrue() {
        when(userRepository.getByUserName("testUser")).thenReturn(Optional.of(user));

        authenticationService.authenticate("testUser", "password123");
        assertTrue(authenticationService.isAuthenticated("testUser"));
    }

    @Test
    void isAuthenticated_InvalidUserName_ReturnsFalse() {
        assertFalse(authenticationService.isAuthenticated("nonExistentUser"));
    }
}
