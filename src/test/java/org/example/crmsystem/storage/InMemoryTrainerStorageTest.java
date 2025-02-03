package org.example.crmsystem.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.model.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTrainerStorageTest {
    private static final String TEST_FILE_PATH = "src/test/resources/test_trainer_storage.json";
    private static final String TEST_EMPTY_FILE_PATH = "src/test/resources/test_empty_storage.json";

    private InMemoryTrainerStorage inMemoryTrainerStorage;
    private static ObjectMapper mapper;
    private Trainer trainer;

    @BeforeAll
    static void setUpBeforeClass() {
        mapper = new ObjectMapper();

        File testFile = new File(TEST_FILE_PATH);
        testFile.setWritable(false);
    }

    @BeforeEach
    void setUp() {
        inMemoryTrainerStorage = new InMemoryTrainerStorage(mapper, TEST_FILE_PATH);
        inMemoryTrainerStorage.init();

        trainer = Trainer.builder()
                .firstName("Andrew")
                .userName("Andrew.Montgomery")
                .lastName("Montgomery")
                .specialization(TrainingType.FITNESS)
                .isActive(true)
                .build();
    }

    @Test
    void testInit_FileNotEmpty_ListNotEmpty() {
        inMemoryTrainerStorage.init();

        assertEquals(2, inMemoryTrainerStorage.getAll().size());
        assertTrue(inMemoryTrainerStorage.getById(1).isPresent());
        assertTrue(inMemoryTrainerStorage.getById(2).isPresent());
    }

    @Test
    void testInit_FileEmpty_ExceptionResolving() {
        inMemoryTrainerStorage = new InMemoryTrainerStorage(mapper, TEST_EMPTY_FILE_PATH);
        inMemoryTrainerStorage.init();

        assertEquals(0, inMemoryTrainerStorage.getAll().size());
        assertFalse(inMemoryTrainerStorage.getById(1).isPresent());
        assertFalse(inMemoryTrainerStorage.getById(2).isPresent());
    }

    @Test
    void testAddTrainer_Successful() {
        Trainer addedTrainer = inMemoryTrainerStorage.add(trainer);

        assertEquals(3, inMemoryTrainerStorage.getAll().size());
        assertTrue(addedTrainer.getTrainerId() > 0, "Trainer ID should be assigned");
        assertEquals(3, addedTrainer.getTrainerId(), "Trainer ID should be assigned by increment");
    }

    @Test
    void testGetById_Successful() {
        Optional<Trainer> foundTrainer = inMemoryTrainerStorage.getById(1);

        assertTrue(foundTrainer.isPresent(), "Trainer should be present");
        assertEquals("Bob.Ross", foundTrainer.get().getUserName(), "Trainer username should match");
    }

    @Test
    void testUpdateTrainer_TrainerFound_Successful() throws EntityNotFoundException {
        Trainer trainer = inMemoryTrainerStorage.getById(2).get();
        TrainingType newSpecialization = TrainingType.FITNESS;
        trainer.setSpecialization(newSpecialization);

        Trainer updatedTrainer = inMemoryTrainerStorage.update(trainer);

        assertEquals(newSpecialization, updatedTrainer.getSpecialization(), "Updated trainer specialization should match");
    }

    @Test
    void testUpdateTrainer_TrainerNotFound_ThrowsException() {
        trainer.setTrainerId(100);

        Exception ex = assertThrows(EntityNotFoundException.class, () -> inMemoryTrainerStorage.update(trainer));
        assertEquals("Trainer with ID 100 not found.", ex.getMessage(), "Expected error message");

    }

    @Test
    void testDeleteById_TrainerFound_True() {
        boolean isDeleted = inMemoryTrainerStorage.deleteById(2);

        assertTrue(isDeleted, "Trainer should be deleted");
        assertFalse(inMemoryTrainerStorage.getById(2).isPresent(), "Deleted trainer should not be found");
    }

    @Test
    void testDeleteById_TrainerNotFound_False() {
        boolean isDeleted = inMemoryTrainerStorage.deleteById(100);

        assertFalse(isDeleted, "Trainer should not be deleted, because it does not exist");
        assertFalse(inMemoryTrainerStorage.getById(100).isPresent(), "Deleted trainer should not be found");
    }

    @Test
    void testGetByName_TrainersFound() {
        List<Trainer> result = inMemoryTrainerStorage.getByName("Bob.Ross");

        assertEquals(1, result.size(), "Should find one trainer");
    }

    @Test
    void testGetByName_TrainersNotFound() {
        List<Trainer> result = inMemoryTrainerStorage.getByName("Olga.Gratilova");

        assertEquals(0, result.size(), "Should not find trainers");
    }

    @Test
    void testGetAll_Successful() {
        List<Trainer> result = inMemoryTrainerStorage.getAll();

        assertEquals(2, result.size(), "Should find 2 trainer");
    }

    @Test
    void testAddTraining_TrainerFound_Successful() throws EntityNotFoundException {
        Training training = Training.builder().trainingId(1).trainerId(1).trainerId(1).build();

        inMemoryTrainerStorage.addTraining(1, training.getTrainingId());
        Trainer trainerWithTraining = inMemoryTrainerStorage.getById(1).get();

        assertNotNull(trainerWithTraining.getTrainerTrainings(), "Trainer trainings list should not be null");
        assertFalse(trainerWithTraining.getTrainerTrainings().isEmpty());
        assertTrue(trainerWithTraining.getTrainerTrainings().contains(1L), "Training ID should be added to the trainer");
    }

    @Test
    void testAddTraining_TrainerNotFound_ThrowsException() {
        Training training = Training.builder().trainingId(1).trainerId(100).traineeId(1).build();

        assertThrows(EntityNotFoundException.class, () -> inMemoryTrainerStorage.addTraining(100, training.getTrainingId()));
    }
}
