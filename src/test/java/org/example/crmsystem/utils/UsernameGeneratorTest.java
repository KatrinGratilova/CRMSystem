package org.example.crmsystem.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.crmsystem.dao.*;
import org.example.crmsystem.dao.interfaces.*;
import org.example.crmsystem.model.*;
import org.example.crmsystem.storage.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UsernameGeneratorTest {
    private static final String TRAINEE_FILE_PATH = "src/test/resources/test_trainee_storage.json";
    private static final String TRAINER_FILE_PATH = "src/test/resources/test_trainer_storage.json";

    private static UsernameGenerator usernameGenerator;

    private Trainee trainee;
    private Trainer trainer;

    @BeforeAll
    static void setUpBeforeClass() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        InMemoryTraineeStorage inMemoryTraineeStorage = new InMemoryTraineeStorage(mapper, TRAINEE_FILE_PATH);
        InMemoryTrainerStorage inMemoryTrainerStorage = new InMemoryTrainerStorage(mapper, TRAINER_FILE_PATH);
        inMemoryTraineeStorage.init();
        inMemoryTrainerStorage.init();

        TraineeDAO traineeDAO = new TraineeDAOImpl(inMemoryTraineeStorage, inMemoryTraineeStorage);
        TrainerDAO trainerDAO = new TrainerDAOImpl(inMemoryTrainerStorage, inMemoryTrainerStorage);
        usernameGenerator = new UsernameGenerator(traineeDAO, trainerDAO);
    }

    @BeforeEach
    void setUp() {
        trainee = Trainee.builder()
                .dateOfBirth(LocalDate.of(2000, 12, 12))
                .address("Odesa, st. Bunina")
                .firstName("Andrew")
                .lastName("Montgomery")
                .isActive(true)
                .build();
        trainer = Trainer.builder()
                .firstName("Bob")
                .lastName("Ross")
                .specialization(TrainingType.FITNESS)
                .isActive(true)
                .build();
    }

    @Test
    void testGenerateUserName_ShouldGenerateUniqueUserNameWhenNoConflict() {
        String generatedUserName = usernameGenerator.generateUserName(trainee);

        assertEquals("Andrew.Montgomery", generatedUserName, "Username should match expected format");
    }

    @Test
    void testGenerateUserName_ShouldGenerateUserNameWithSuffixWhenConflictExists() {
        String generatedUserName = usernameGenerator.generateUserName(trainer);

        assertEquals("Bob.Ross1", generatedUserName, "Username should have a numeric suffix");
    }
}
