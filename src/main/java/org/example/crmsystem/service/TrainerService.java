package org.example.crmsystem.service;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.example.crmsystem.converter.TrainerConverter;
import org.example.crmsystem.converter.TrainingConverter;
import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.dao.interfaces.TrainerRepositoryCustom;
import org.example.crmsystem.dto.trainer.TrainerServiceDTO;
import org.example.crmsystem.dto.training.TrainingByTrainerDTO;
import org.example.crmsystem.dto.user.UserUpdateStatusRequestDTO;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.TrainingEntity;
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
public class TrainerService {
    private final TrainerDAO trainerRepository;
    private final TrainerRepositoryCustom trainerRepositoryCustom;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;
    private final AuthenticationService authenticationService;

    public TrainerService(TrainerDAO trainerRepository, TrainerRepositoryCustom trainerRepositoryCustom, PasswordGenerator passwordGenerator, UsernameGenerator usernameGenerator, AuthenticationService authenticationService, MeterRegistry meterRegistry) {
        this.trainerRepository = trainerRepository;
        this.trainerRepositoryCustom = trainerRepositoryCustom;
        this.passwordGenerator = passwordGenerator;
        this.usernameGenerator = usernameGenerator;
        this.authenticationService = authenticationService;

        Gauge.builder("trainer.count", trainerRepository::count)
                .description("The number of trainers")
                .register(meterRegistry);

        Gauge.builder("active.trainer.count", () -> trainerRepository.findByActive(true).size())
                .description("The number of active trainers")
                .register(meterRegistry);
    }

    public TrainerServiceDTO createProfile(TrainerServiceDTO trainerDTO) {
        String transactionId = ThreadContext.get("transactionId");
        log.debug(LogMessages.ADDED_NEW_TRAINER.getMessage(), transactionId, trainerDTO.getFirstName());

        trainerDTO.setUsername(usernameGenerator.generateUsername(trainerDTO));
        trainerDTO.setPassword(passwordGenerator.generateUserPassword());

        TrainerEntity addedTrainerEntity = trainerRepository.save(TrainerConverter.toEntity(trainerDTO));
        authenticationService.authenticate(trainerDTO.getUsername(), trainerDTO.getPassword());

        log.info(LogMessages.ADDED_NEW_TRAINER.getMessage(), transactionId, addedTrainerEntity.getUsername());
        return TrainerConverter.toServiceDTO(addedTrainerEntity);
    }

    public TrainerServiceDTO getByUsername(String username) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");
        log.debug(LogMessages.RETRIEVING_TRAINER.getMessage(), transactionId, username);

        Optional<TrainerEntity> trainer = trainerRepositoryCustom.getByUsername(username);
        if (trainer.isEmpty()) {
            log.warn(LogMessages.TRAINER_NOT_FOUND.getMessage(), transactionId, username);
            throw new jakarta.persistence.EntityNotFoundException(ExceptionMessages.TRAINER_NOT_FOUND.format(username));
        } else {
            log.info(LogMessages.TRAINER_FOUND.getMessage(), transactionId, trainer.get().getUsername());
            return TrainerConverter.toServiceDTO(trainer.get());
        }
    }

    public TrainerServiceDTO update(TrainerServiceDTO trainerDTO) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");
        log.debug(LogMessages.ATTEMPTING_TO_UPDATE_TRAINER.getMessage(), transactionId, trainerDTO.getUsername());

        TrainerEntity updatedTrainerEntity = TrainerConverter.toEntity(trainerDTO);
        try {
            updatedTrainerEntity = trainerRepositoryCustom.update(updatedTrainerEntity);
        } catch (EntityNotFoundException e) {
            log.warn(LogMessages.TRAINER_NOT_FOUND.getMessage(), transactionId, updatedTrainerEntity.getUsername());
            throw e;
        }
        log.info(LogMessages.UPDATED_TRAINER.getMessage(), transactionId, updatedTrainerEntity.getUsername());
        return TrainerConverter.toServiceDTO(updatedTrainerEntity);
    }

    public boolean toggleActiveStatus(String username, UserUpdateStatusRequestDTO trainerStatus) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");
        log.debug(LogMessages.ATTEMPTING_TO_CHANGE_TRAINERS_STATUS.getMessage(), transactionId, username);

        boolean result;
        try {
            result = trainerRepositoryCustom.toggleActiveStatus(username, trainerStatus.getIsActive());
        } catch (EntityNotFoundException e) {
            log.warn(LogMessages.TRAINER_NOT_FOUND.getMessage(), transactionId, username);
            throw e;
        }
        log.info(LogMessages.TRAINERS_STATUS_CHANGED.getMessage(), transactionId, username, result);
        return result;
    }

    public List<TrainingByTrainerDTO> getTrainerTrainingsByCriteria(
            String trainerUsername, LocalDateTime fromDate, LocalDateTime toDate, String traineeName) {
        String transactionId = ThreadContext.get("transactionId");
        log.debug(LogMessages.FETCHING_TRAININGS_FOR_TRAINER_BY_CRITERIA.getMessage(), transactionId,
                trainerUsername, fromDate, toDate, traineeName);

        List<TrainingEntity> trainings = trainerRepositoryCustom.getTrainerTrainingsByCriteria(
                trainerUsername, fromDate, toDate, traineeName);

        log.info(LogMessages.FOUND_TRAININGS_FOR_TRAINER.getMessage(), transactionId, trainings.size(), trainerUsername);
        return trainings.stream().map(TrainingConverter::toByTrainerDTO).collect(Collectors.toList());
    }
}
