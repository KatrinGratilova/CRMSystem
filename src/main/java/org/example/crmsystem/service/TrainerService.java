package org.example.crmsystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.example.crmsystem.converter.TrainerConverter;
import org.example.crmsystem.converter.TrainingConverter;
import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.dto.trainer.TrainerServiceDTO;
import org.example.crmsystem.dto.training.TrainingByTrainerDTO;
import org.example.crmsystem.dto.user.UserUpdateStatusRequestDTO;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.TrainingEntity;
import jakarta.persistence.EntityNotFoundException;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.messages.LogMessages;
import org.example.crmsystem.utils.PasswordGenerator;
import org.example.crmsystem.utils.UsernameGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class TrainerService {
    private final TrainerDAO trainerRepository;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;
    private final AuthenticationService authenticationService;

    public TrainerServiceDTO createProfile(TrainerServiceDTO trainerDTO) {
        String transactionId = ThreadContext.get("transactionId");
        log.debug(LogMessages.ADDED_NEW_TRAINER.getMessage(), transactionId, trainerDTO.getFirstName());

        trainerDTO.setUserName(usernameGenerator.generateUserName(trainerDTO));
        trainerDTO.setPassword(passwordGenerator.generateUserPassword());

        TrainerEntity addedTrainerEntity = trainerRepository.add(TrainerConverter.toEntity(trainerDTO));
        authenticationService.authenticate(trainerDTO.getUserName(), trainerDTO.getPassword());

        log.info(LogMessages.ADDED_NEW_TRAINER.getMessage(), addedTrainerEntity.getId());
        return TrainerConverter.toServiceDTO(addedTrainerEntity);
    }

    public TrainerServiceDTO getByUsername(String username) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");
        log.debug(LogMessages.RETRIEVING_TRAINER.getMessage(), transactionId, username);

        Optional<TrainerEntity> trainer = trainerRepository.getByUserName(username);
        if (trainer.isEmpty()) {
            log.warn(LogMessages.TRAINER_NOT_FOUND.getMessage(), transactionId, username);
            throw new jakarta.persistence.EntityNotFoundException(ExceptionMessages.TRAINER_NOT_FOUND_BY_USERNAME.format(username));
        } else {
            log.info(LogMessages.TRAINER_FOUND.getMessage(), trainer.get().getId());
            return TrainerConverter.toServiceDTO(trainer.get());
        }
    }

    public TrainerServiceDTO update(TrainerServiceDTO trainerDTO) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");
        log.debug(LogMessages.ATTEMPTING_TO_UPDATE_TRAINER.getMessage(), transactionId, trainerDTO.getUserName());

        TrainerEntity updatedTrainerEntity = TrainerConverter.toEntity(trainerDTO);
        try {
            updatedTrainerEntity = trainerRepository.update(updatedTrainerEntity);
        } catch (EntityNotFoundException e) {
            log.warn(LogMessages.TRAINER_NOT_FOUND.getMessage(), transactionId, updatedTrainerEntity.getUserName());
            throw e;
        }
        log.info(LogMessages.UPDATED_TRAINER.getMessage(), transactionId, updatedTrainerEntity.getUserName());
        return TrainerConverter.toServiceDTO(updatedTrainerEntity);
    }

    public boolean toggleActiveStatus(String username, UserUpdateStatusRequestDTO trainerStatus) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");
        log.debug(LogMessages.ATTEMPTING_TO_CHANGE_TRAINERS_STATUS.getMessage(), transactionId, username);

        boolean result;
        try {
            result = trainerRepository.toggleActiveStatus(username, trainerStatus.getIsActive());
        } catch (EntityNotFoundException e) {
            log.warn(LogMessages.TRAINER_NOT_FOUND.getMessage(), transactionId, username);
            throw e;
        }
        log.info(LogMessages.TRAINERS_STATUS_CHANGED.getMessage(), transactionId, username, result);
        return result;
    }

    public List<TrainingByTrainerDTO> getTrainerTrainingsByCriteria(
            String trainerUserName, LocalDateTime fromDate, LocalDateTime toDate, String traineeName) {
        String transactionId = ThreadContext.get("transactionId");
        log.debug(LogMessages.FETCHING_TRAININGS_FOR_TRAINER_BY_CRITERIA.getMessage(), transactionId,
                trainerUserName, fromDate, toDate, traineeName);

        List<TrainingEntity> trainings = trainerRepository.getTrainerTrainingsByCriteria(
                trainerUserName, fromDate, toDate, traineeName);

        log.info(LogMessages.FOUND_TRAININGS_FOR_TRAINER.getMessage(), transactionId, trainings.size(), trainerUserName);
        return trainings.stream().map(TrainingConverter::toByTrainerDTO).collect(Collectors.toList());
    }
}
