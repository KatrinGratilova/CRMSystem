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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

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
        when(trainingTypeRepository.getById(id)).thenReturn(mockEntity);

        TrainingTypeEntity result = trainingTypeService.getById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(TrainingType.YOGA, result.getTrainingType());

        verify(trainingTypeRepository).getById(id);
    }

    @Test
    public void testGetAll(){
        when(trainingTypeRepository.getAll()).thenReturn(List.of(new TrainingTypeEntity(1, TrainingType.YOGA)));

        List<TrainingTypeEntity> result = trainingTypeService.getAll();
        assertEquals(1, result.size());
        assertEquals(TrainingType.YOGA, result.get(0).getTrainingType());
        verify(trainingTypeRepository, times(1)).getAll();
    }
}
