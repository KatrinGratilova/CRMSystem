package org.example.crmsystem.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.model.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTrainingStorageTest {
    private static final String TEST_FILE_PATH = "src/test/resources/test_training_storage.json";
    private static final String TEST_EMPTY_FILE_PATH = "src/test/resources/test_empty_storage.json";

    private InMemoryTrainingStorage inMemoryTrainingStorage;
    private static ObjectMapper mapper;
    private Training training;

    @BeforeAll
    static void setUpBeforeClass() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        File testFile = new File(TEST_FILE_PATH);
        testFile.setWritable(false);
    }

    @BeforeEach
    void setUp() {
        inMemoryTrainingStorage = new InMemoryTrainingStorage(mapper, TEST_FILE_PATH);
        inMemoryTrainingStorage.init();

        training = Training.builder()
                .trainingName("First Pilates Training")
                .trainingDate(LocalDateTime.of(2024, 12, 12, 12, 0, 0))
                .trainingType(TrainingType.PILATES)
                .trainingTime(120)
                .build();
    }

    @Test
    void testInit_FileNotEmpty_ListNotEmpty() {
        inMemoryTrainingStorage.init();

        assertEquals(2, inMemoryTrainingStorage.getAll().size());
        assertTrue(inMemoryTrainingStorage.getById(1).isPresent());
        assertTrue(inMemoryTrainingStorage.getById(2).isPresent());
    }

    @Test
    void testInit_FileEmpty_ExceptionResolving() {
        inMemoryTrainingStorage = new InMemoryTrainingStorage(mapper, TEST_EMPTY_FILE_PATH);
        inMemoryTrainingStorage.init();

        assertEquals(0, inMemoryTrainingStorage.getAll().size());
        assertFalse(inMemoryTrainingStorage.getById(1).isPresent());
        assertFalse(inMemoryTrainingStorage.getById(2).isPresent());
    }


    @Test
    void testAddTraining_Successful() {
        Training addedTraining = inMemoryTrainingStorage.add(training);

        assertEquals(3, inMemoryTrainingStorage.getAll().size());
        assertTrue(addedTraining.getTrainingId() > 0, "Training ID should be assigned");
        assertEquals(3, addedTraining.getTrainingId(), "Training ID should be assigned by increment");
    }

    @Test
    void testGetById_Successful() {
        Optional<Training> foundTraining = inMemoryTrainingStorage.getById(1);

        assertTrue(foundTraining.isPresent(), "Training should be present");
        assertEquals("First Yoga Training", foundTraining.get().getTrainingName(), "Training username should match");
    }

    @Test
    void testUpdateTraining_TrainingFound_Successful() throws EntityNotFoundException {
        Training training = inMemoryTrainingStorage.getById(2).get();
        training.setTraineeId(1);
        training.setTrainerId(1);

        Training updatedTraining = inMemoryTrainingStorage.update(training);

        assertEquals(1, updatedTraining.getTraineeId(), "Updated trainee id should match");
        assertEquals(1, updatedTraining.getTrainerId(), "Updated trainer id should match");
    }

    @Test
    void testUpdateTraining_TrainingNotFound_ThrowsException() {
        training.setTrainingId(100);

        Exception ex = assertThrows(EntityNotFoundException.class, () -> inMemoryTrainingStorage.update(training));
        assertEquals("Training with ID 100 not found.", ex.getMessage(), "Expected error message");

    }

    @Test
    void testDeleteById_TrainingFound_True() {
        boolean isDeleted = inMemoryTrainingStorage.deleteById(2);

        assertTrue(isDeleted, "Training should be deleted");
        assertFalse(inMemoryTrainingStorage.getById(2).isPresent(), "Deleted training should not be found");
    }

    @Test
    void testDeleteById_TrainingNotFound_False() {
        boolean isDeleted = inMemoryTrainingStorage.deleteById(100);

        assertFalse(isDeleted, "Training should not be deleted, because it does not exist");
        assertFalse(inMemoryTrainingStorage.getById(100).isPresent(), "Deleted training should not be found");
    }

    @Test
    void testGetByName_TrainingsFound() {
        List<Training> result = inMemoryTrainingStorage.getByName("First Zumba Training");

        assertEquals(1, result.size(), "Should find one training");
    }

    @Test
    void testGetByName_TrainingsNotFound() {
        List<Training> result = inMemoryTrainingStorage.getByName("First Ballet Training");

        assertEquals(0, result.size(), "Should not find trainings");
    }

    @Test
    void testGetAll_Successful() {
        List<Training> result = inMemoryTrainingStorage.getAll();

        assertEquals(2, result.size(), "Should find 2 training");
    }
}
