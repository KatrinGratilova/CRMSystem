//package org.example.crmsystem.utils;
//
//import org.example.crmsystem.converter.TraineeConverter;
//import org.example.crmsystem.converter.TrainerConverter;
//import org.example.crmsystem.dao.interfaces.TraineeRepositoryCustom;
//import org.example.crmsystem.entity.TraineeEntity;
//import org.example.crmsystem.entity.TrainerEntity;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class UsernameGeneratorTest {
//    @Mock
//    private TraineeRepositoryCustom traineeDAO;
//
//    @Mock
//    private TraineeRepositoryCustom trainerDAO;
//
//    @InjectMocks
//    private UsernameGenerator usernameGenerator;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void generateUserName_ShouldReturnBaseName_WhenNoDuplicatesExist() {
//        TrainerEntity user = new TrainerEntity();
//        user.setFirstName("John");
//        user.setLastName("Doe");
//
//        when(traineeDAO.getWhereUsernameStartsWith("John.Doe")).thenReturn(Collections.emptyList());
//        when(trainerDAO.getWhereUsernameStartsWith("John.Doe")).thenReturn(Collections.emptyList());
//
//        String generatedUsername = usernameGenerator.generateUsername(TrainerConverter.toServiceDTO(user));
//
//        assertEquals("John.Doe", generatedUsername);
//        verify(traineeDAO).getWhereUsernameStartsWith("John.Doe");
//        verify(trainerDAO).getWhereUsernameStartsWith("John.Doe");
//    }
//
//    @Test
//    void generateUserName_ShouldAppendNumber_WhenDuplicatesExist() {
//        TraineeEntity user = new TraineeEntity();
//        user.setFirstName("Jane");
//        user.setLastName("Smith");
//
//        when(traineeDAO.getWhereUsernameStartsWith("Jane.Smith")).thenReturn(List.of(TraineeEntity
//                .builder()
//                .username("Jane.Smith")
//                .build()));
//        when(trainerDAO.getWhereUsernameStartsWith("Jane.Smith")).thenReturn(new ArrayList<>());
//
//        String generatedUsername = usernameGenerator.generateUsername(TraineeConverter.toServiceDTO(user));
//
//        assertEquals("Jane.Smith1", generatedUsername);
//        verify(traineeDAO).getWhereUsernameStartsWith("Jane.Smith");
//        verify(trainerDAO).getWhereUsernameStartsWith("Jane.Smith");
//    }
//}
