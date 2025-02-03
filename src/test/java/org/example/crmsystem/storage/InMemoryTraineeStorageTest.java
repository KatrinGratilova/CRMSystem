package org.example.crmsystem.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.model.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTraineeStorageTest {
    private static final String TEST_FILE_PATH = "src/test/resources/test_trainee_storage.json";
    private static final String TEST_EMPTY_FILE_PATH = "src/test/resources/test_empty_storage.json";

    private InMemoryTraineeStorage inMemoryTraineeStorage;
    private static ObjectMapper mapper;
    private Trainee trainee;

    @BeforeAll
    static void setUpBeforeClass() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        File testFile = new File(TEST_FILE_PATH);
        testFile.setWritable(false);
    }

    @BeforeEach
    void setUp() {
        inMemoryTraineeStorage = new InMemoryTraineeStorage(mapper, TEST_FILE_PATH);
        inMemoryTraineeStorage.init();

        trainee = Trainee.builder()
                .dateOfBirth(LocalDate.of(2000, 12, 12))
                .address("Odesa, st. Bunina")
                .firstName("Andrew")
                .userName("Andrew.Montgomery")
                .lastName("Montgomery")
                .isActive(true)
                .build();
    }

    @Test
    void testInit_FileNotEmpty_ListNotEmpty() {
        inMemoryTraineeStorage.init();

        assertEquals(2, inMemoryTraineeStorage.getAll().size());
        assertTrue(inMemoryTraineeStorage.getById(1).isPresent());
        assertTrue(inMemoryTraineeStorage.getById(2).isPresent());
    }

    @Test
    void testInit_FileEmpty_ExceptionResolving() {
        inMemoryTraineeStorage = new InMemoryTraineeStorage(mapper, TEST_EMPTY_FILE_PATH);
        inMemoryTraineeStorage.init();

        assertEquals(0, inMemoryTraineeStorage.getAll().size());
        assertFalse(inMemoryTraineeStorage.getById(1).isPresent());
        assertFalse(inMemoryTraineeStorage.getById(2).isPresent());
    }

    @Test
    void testAddTrainee_Successful() {
        Trainee addedTrainee = inMemoryTraineeStorage.add(trainee);

        assertEquals(3, inMemoryTraineeStorage.getAll().size());
        assertTrue(addedTrainee.getTraineeId() > 0, "Trainee ID should be assigned");
        assertEquals(3, addedTrainee.getTraineeId(), "Trainee ID should be assigned by increment");
    }

    @Test
    void testGetById_Successful() {
        Optional<Trainee> foundTrainee = inMemoryTraineeStorage.getById(1);

        assertTrue(foundTrainee.isPresent(), "Trainee should be present");
        assertEquals("Kate.Gratilova", foundTrainee.get().getUserName(), "Trainee username should match");
    }

    @Test
    void testUpdateTrainee_TraineeFound_Successful() throws EntityNotFoundException {
        Trainee trainee = inMemoryTraineeStorage.getById(2).get();
        String newAddress = "Odesa, st. Shevchenko";
        trainee.setAddress(newAddress);

        Trainee updatedTrainee = inMemoryTraineeStorage.update(trainee);

        assertEquals(newAddress, updatedTrainee.getAddress(), "Updated trainee address should match");
    }

    @Test
    void testUpdateTrainee_TraineeNotFound_ThrowsException() {
        trainee.setTraineeId(100);

        Exception ex = assertThrows(EntityNotFoundException.class, () -> inMemoryTraineeStorage.update(trainee));
        assertEquals("Trainee with ID 100 not found.", ex.getMessage(), "Expected error message");

    }

    @Test
    void testDeleteById_TraineeFound_True() {
        boolean isDeleted = inMemoryTraineeStorage.deleteById(2);

        assertTrue(isDeleted, "Trainee should be deleted");
        assertFalse(inMemoryTraineeStorage.getById(2).isPresent(), "Deleted trainee should not be found");
    }

    @Test
    void testDeleteById_TraineeNotFound_False() {
        boolean isDeleted = inMemoryTraineeStorage.deleteById(100);

        assertFalse(isDeleted, "Trainee should not be deleted, because it does not exist");
        assertFalse(inMemoryTraineeStorage.getById(100).isPresent(), "Deleted trainee should not be found");
    }

    @Test
    void testGetByName_TraineeFound() {
        List<Trainee> result = inMemoryTraineeStorage.getByName("Kate.Gratilova");

        assertEquals(1, result.size(), "Should find one trainee");
    }

    @Test
    void testGetByName_TraineeNotFound() {
        List<Trainee> result = inMemoryTraineeStorage.getByName("Olga.Gratilova");

        assertEquals(0, result.size(), "Should not find trainees");
    }

    @Test
    void testGetAll_Successful() {
        List<Trainee> result = inMemoryTraineeStorage.getAll();

        assertEquals(2, result.size(), "Should find 2 trainee");
    }

    @Test
    void testAddTraining_TraineeFound_Successful() throws EntityNotFoundException {
        Training training = Training.builder().trainingId(1).traineeId(1).trainerId(1).build();

        inMemoryTraineeStorage.addTraining(1, training.getTrainingId());
        Trainee traineeWithTraining = inMemoryTraineeStorage.getById(1).get();

        assertNotNull(traineeWithTraining.getTraineeTrainings(), "Trainee trainings list should not be null");
        assertFalse(traineeWithTraining.getTraineeTrainings().isEmpty());
        assertTrue(traineeWithTraining.getTraineeTrainings().contains(1L), "Training ID should be added to the trainee");
    }

    @Test
    void testAddTraining_TraineeNotFound_ThrowsException() {
        Training training = Training.builder().trainingId(1).traineeId(100).trainerId(1).build();

        assertThrows(EntityNotFoundException.class, () -> inMemoryTraineeStorage.addTraining(100, training.getTrainingId()));
    }
}
