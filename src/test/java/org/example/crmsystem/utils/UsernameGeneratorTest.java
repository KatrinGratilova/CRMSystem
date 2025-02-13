package org.example.crmsystem.utils;

import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsernameGeneratorTest {
    @Mock
    private TraineeDAO traineeDAO;

    @Mock
    private TrainerDAO trainerDAO;

    @InjectMocks
    private UsernameGenerator usernameGenerator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateUserName_ShouldReturnBaseName_WhenNoDuplicatesExist() {
        UserEntity user = new UserEntity();
        user.setFirstName("John");
        user.setLastName("Doe");

        when(traineeDAO.getWhereUserNameStartsWith("John.Doe")).thenReturn(Collections.emptyList());
        when(trainerDAO.getWhereUserNameStartsWith("John.Doe")).thenReturn(Collections.emptyList());

        String generatedUsername = usernameGenerator.generateUserName(user);

        assertEquals("John.Doe", generatedUsername);
        verify(traineeDAO).getWhereUserNameStartsWith("John.Doe");
        verify(trainerDAO).getWhereUserNameStartsWith("John.Doe");
    }

    @Test
    void generateUserName_ShouldAppendNumber_WhenDuplicatesExist() {
        UserEntity user = new UserEntity();
        user.setFirstName("Jane");
        user.setLastName("Smith");

        when(traineeDAO.getWhereUserNameStartsWith("Jane.Smith")).thenReturn(List.of(TraineeEntity
                .builder()
                .userName("Jane.Smith")
                .build()));
        when(trainerDAO.getWhereUserNameStartsWith("Jane.Smith")).thenReturn(List.of(TrainerEntity
                .builder()
                .userName("Jane.Smith1")
                .build()));

        String generatedUsername = usernameGenerator.generateUserName(user);

        assertEquals("Jane.Smith2", generatedUsername);
        verify(traineeDAO).getWhereUserNameStartsWith("Jane.Smith");
        verify(trainerDAO).getWhereUserNameStartsWith("Jane.Smith");
    }
}
