package org.example.crmsystem.service;

import org.example.crmsystem.dao.TrainingTypeRepository;
import org.example.crmsystem.entity.TrainingType;
import org.example.crmsystem.entity.TrainingTypeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TrainingTypeServiceTest {
    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @InjectMocks
    private TrainingTypeService trainingTypeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInitTrainingTypes() {
        trainingTypeService.initTrainingTypes();

        verify(trainingTypeRepository, times(TrainingType.values().length))
                .add(Mockito.any(TrainingTypeEntity.class));
    }

    @Test
    public void testGetById() {
        int id = 1;
        TrainingTypeEntity mockEntity = new TrainingTypeEntity(1, TrainingType.YOGA);

        Mockito.when(trainingTypeRepository.getById(id)).thenReturn(mockEntity);

        TrainingTypeEntity result = trainingTypeService.getById(id);

        assert result != null;
        assert result.getId() == id;
        assert result.getTrainingType() == TrainingType.YOGA;

        verify(trainingTypeRepository).getById(id);
    }
}
