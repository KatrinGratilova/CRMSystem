package org.example.crmsystem.service;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.example.crmsystem.converter.TraineeConverter;
import org.example.crmsystem.converter.TrainerConverter;
import org.example.crmsystem.converter.TrainingConverter;
import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.dao.interfaces.TraineeRepositoryCustom;
import org.example.crmsystem.dto.trainee.TraineeServiceDTO;
import org.example.crmsystem.dto.trainer.TrainerNestedDTO;
import org.example.crmsystem.dto.training.TrainingByTraineeDTO;
import org.example.crmsystem.dto.user.UserUpdateStatusRequestDTO;
import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.messages.LogMessages;
import org.example.crmsystem.utils.PasswordGenerator;
import org.example.crmsystem.utils.UsernameGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class TraineeService {
    private final TraineeDAO traineeRepository;
    private final TraineeRepositoryCustom traineeRepositoryCustom;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;
    private final TrainerService trainerService;
    private final PasswordEncoder passwordEncoder;

    public TraineeService(TraineeDAO traineeRepository, TraineeRepositoryCustom traineeRepositoryCustom, PasswordGenerator passwordGenerator, UsernameGenerator usernameGenerator, TrainerService trainerService, MeterRegistry meterRegistry, PasswordEncoder passwordEncoder) {
        this.traineeRepository = traineeRepository;
        this.traineeRepositoryCustom = traineeRepositoryCustom;
        this.passwordGenerator = passwordGenerator;
        this.usernameGenerator = usernameGenerator;
        this.trainerService = trainerService;
        this.passwordEncoder = passwordEncoder;

        Gauge.builder("trainee.count", traineeRepository::count)
                .description("The number of trainees")
                .register(meterRegistry);

        Gauge.builder("active.trainee.count", () -> traineeRepository.findByActive(true).size())
                .description("The number of active trainees")
                .register(meterRegistry);
    }

    public TraineeServiceDTO createProfile(TraineeServiceDTO traineeDTO) {
        String transactionId = ThreadContext.get("transactionId");
        log.debug(LogMessages.ATTEMPTING_TO_ADD_NEW_TRAINEE.getMessage(), transactionId, traineeDTO.getFirstName());

        String password = passwordGenerator.generateUserPassword();
        traineeDTO.setUsername(usernameGenerator.generateUsername(traineeDTO));
        traineeDTO.setPassword(passwordEncoder.encode(password));
        traineeDTO.setActive(true);

        TraineeEntity addedTraineeEntity = traineeRepository.save(TraineeConverter.toEntity(traineeDTO));
        TraineeServiceDTO resultDTO = TraineeConverter.toServiceDTO(addedTraineeEntity);
        resultDTO.setPassword(password);

        log.info(LogMessages.ADDED_NEW_TRAINEE.getMessage(), transactionId, resultDTO.getUsername());

        return resultDTO;
    }

    public TraineeServiceDTO getByUsername(String username) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");
        log.debug(LogMessages.RETRIEVING_TRAINEE.getMessage(), transactionId, username);

        Optional<TraineeEntity> trainee = traineeRepositoryCustom.getByUsername(username);
        if (trainee.isEmpty()) {
            log.warn(LogMessages.TRAINEE_NOT_FOUND.getMessage(), transactionId, username);
            throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(username));
        } else {
            log.info(LogMessages.TRAINEE_FOUND.getMessage(), transactionId, trainee.get().getUsername());
            return TraineeConverter.toServiceDTO(trainee.get());
        }
    }

    public TraineeServiceDTO update(TraineeServiceDTO traineeDTO) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");
        log.debug(LogMessages.ATTEMPTING_TO_UPDATE_TRAINEE.getMessage(), transactionId, traineeDTO.getUsername());

        TraineeEntity updatedTraineeEntity = TraineeConverter.toEntity(traineeDTO);
        try {
            updatedTraineeEntity = traineeRepositoryCustom.update(updatedTraineeEntity);
        } catch (EntityNotFoundException e) {
            log.warn(LogMessages.TRAINEE_NOT_FOUND.getMessage(), transactionId, updatedTraineeEntity.getUsername());
            throw e;
        }
        log.info(LogMessages.UPDATED_TRAINEE.getMessage(), transactionId, updatedTraineeEntity.getUsername());
        return TraineeConverter.toServiceDTO(updatedTraineeEntity);
    }

    public void deleteByUsername(String username) {
        String transactionId = ThreadContext.get("transactionId");
        log.debug(LogMessages.ATTEMPTING_TO_DELETE_TRAINEE.getMessage(), transactionId, username);

        traineeRepositoryCustom.deleteByUsername(username);
        log.info(LogMessages.DELETED_TRAINEE.getMessage(), transactionId, username);
    }

    public boolean toggleActiveStatus(String username, UserUpdateStatusRequestDTO traineeStatus) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");
        log.debug(LogMessages.ATTEMPTING_TO_CHANGE_TRAINEES_STATUS.getMessage(), transactionId, username);

        boolean result;
        try {
            result = traineeRepositoryCustom.toggleActiveStatus(username, traineeStatus.getIsActive());
        } catch (EntityNotFoundException e) {
            log.warn(LogMessages.TRAINEE_NOT_FOUND.getMessage(), transactionId, username);
            throw e;
        }
        log.info(LogMessages.TRAINEES_STATUS_CHANGED.getMessage(), transactionId, username, result);
        return result;
    }

    public List<TrainingByTraineeDTO> getTraineeTrainingsByCriteria(
            String traineeUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType) {
        String transactionId = ThreadContext.get("transactionId");
        log.debug(LogMessages.FETCHING_TRAININGS_FOR_TRAINEE_BY_CRITERIA.getMessage(),
                transactionId, traineeUsername, fromDate, toDate, trainerName, trainingType);

        List<TrainingEntity> trainings = traineeRepositoryCustom.getTraineeTrainingsByCriteria(
                traineeUsername, fromDate, toDate, trainerName, trainingType);

        log.info(LogMessages.FOUND_TRAININGS_FOR_TRAINEE.getMessage(), transactionId, trainings.size(), traineeUsername);
        return trainings.stream().map(TrainingConverter::toByTraineeDTO).collect(Collectors.toList());
    }

    public List<TrainerNestedDTO> getTrainersNotAssignedToTrainee(String traineeUsername) {
        String transactionId = ThreadContext.get("transactionId");
        log.debug(LogMessages.FETCHING_TRAINERS_NOT_ASSIGNED_TO_TRAINEE.getMessage(), transactionId, traineeUsername);

        List<TrainerEntity> trainers = traineeRepositoryCustom.getTrainersNotAssignedToTrainee(traineeUsername);
        List<TrainerNestedDTO> trainerDTOs = trainers.stream().map(TrainerConverter::toNestedDTO).collect(Collectors.toList());

        log.info(LogMessages.FOUND_TRAINERS_NOT_ASSIGNED_TO_TRAINEE.getMessage(), transactionId, trainers.size(), traineeUsername);
        return trainerDTOs;
    }

    public List<TrainerNestedDTO> updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");
        log.debug(LogMessages.ATTEMPTING_TO_UPDATE_TRAINEE_TRAINERS.getMessage(), transactionId, traineeUsername);

        TraineeEntity trainee = traineeRepositoryCustom.getByUsername(traineeUsername)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(traineeUsername)));

        List<TrainerEntity> trainerEntities = trainerUsernames.stream()
                .map(trainerService::getByUsername)
                .map(TrainerConverter::toEntity)
                .collect(Collectors.toList());

        trainee.setTrainers(trainerEntities);

        log.info(LogMessages.TRAINEE_TRAINERS_UPDATED.getMessage(), transactionId, traineeUsername);
        return traineeRepositoryCustom.updateTrainers(trainee).getTrainers().stream().map(TrainerConverter::toNestedDTO).collect(Collectors.toList());
    }
}
