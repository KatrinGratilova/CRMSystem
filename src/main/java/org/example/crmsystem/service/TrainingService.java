package org.example.crmsystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.example.crmsystem.converter.TrainingConverter;
import org.example.crmsystem.dao.interfaces.TrainingDAO;
import org.example.crmsystem.dto.training.TrainingServiceDTO;
import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.messages.LogMessages;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class TrainingService {
    private final TrainingDAO trainingRepository;

    public TrainingServiceDTO add(TrainingServiceDTO trainingDTO) {
        String transactionId = ThreadContext.get("transactionId");
        log.debug(LogMessages.ATTEMPTING_TO_ADD_NEW_TRAINING.getMessage(), transactionId, trainingDTO.getTrainingName());

        TrainingEntity trainingEntity = trainingRepository.add(TrainingConverter.toEntity(trainingDTO));

        log.info(LogMessages.ADDED_NEW_TRAINING.getMessage(), transactionId, trainingEntity.getTrainingName());
        return TrainingConverter.toServiceDTO(trainingEntity);
    }
}
