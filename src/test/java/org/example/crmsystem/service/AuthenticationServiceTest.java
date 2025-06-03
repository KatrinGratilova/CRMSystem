//package org.example.crmsystem.service;
//
//import org.example.crmsystem.dao.interfaces.TraineeRepositoryCustom;
//import org.example.crmsystem.dao.interfaces.TrainerRepositoryCustom;
//import org.example.crmsystem.entity.TraineeEntity;
//import org.example.crmsystem.entity.UserEntity;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class AuthenticationServiceTest {
//    @Mock
//    private TraineeRepositoryCustom traineeDAO;
//    @Mock
//    private TrainerRepositoryCustom trainerDAO;
//    @InjectMocks
//    private AuthenticationService authenticationService;
//
//    private UserEntity user;
//
//    @BeforeEach
//    void setUp() {
//        authenticationService = new AuthenticationService(traineeDAO, trainerDAO);
//
//        user = new UserEntity();
//        user.setId(1L);
//        user.setUsername("testUser");
//        user.setPassword("password123");
//    }
//
//    @Test
//    void isAuthenticated_ValidUserName_ReturnsTrue() {
//        when(traineeDAO.getByUsername("testUser")).thenReturn(Optional.of((TraineeEntity) user));
//
//        authenticationService.authenticate("testUser", "password123");
//        assertTrue(authenticationService.isAuthenticated("testUser"));
//    }
//
//    @Test
//    void isAuthenticated_InvalidUserName_ReturnsFalse() {
//        assertFalse(authenticationService.isAuthenticated("nonExistentUser"));
//    }
//}
