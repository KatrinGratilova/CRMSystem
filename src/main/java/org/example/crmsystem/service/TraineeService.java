package org.example.crmsystem.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.converter.*;
import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.dto.trainee.TraineeServiceDTO;
import org.example.crmsystem.dto.trainer.TrainerNestedDTO;
import org.example.crmsystem.dto.training.TrainingByTraineeDTO;
import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.exception.UserIsNotAuthenticated;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.messages.LogMessages;
import org.example.crmsystem.utils.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class TraineeService {
    private final TraineeDAO traineeRepository;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;
    private final AuthenticationService authenticationService;
    private final TrainerService trainerService;

    public TraineeServiceDTO createProfile(TraineeServiceDTO traineeDTO) {
        log.debug(LogMessages.ATTEMPTING_TO_ADD_NEW_TRAINEE.getMessage(), traineeDTO.getFirstName());

        traineeDTO.setPassword(passwordGenerator.generateUserPassword());
        traineeDTO.setUserName(usernameGenerator.generateUserName(traineeDTO));

        TraineeEntity addedTraineeEntity = traineeRepository.add(TraineeConverter.toEntity(traineeDTO));
        authenticationService.authenticate(traineeDTO.getUserName(), traineeDTO.getPassword());

        log.info(LogMessages.ADDED_NEW_TRAINEE.getMessage(), addedTraineeEntity.getId());
        return TraineeConverter.toServiceDTO(addedTraineeEntity);
    }

    public TraineeEntity getById(long id) throws EntityNotFoundException, UserIsNotAuthenticated {
        log.debug(LogMessages.RETRIEVING_TRAINEE.getMessage(), id);

        Optional<TraineeEntity> trainee = traineeRepository.getById(id);

        if (trainee.isEmpty()) {
            log.error(LogMessages.TRAINEE_NOT_FOUND.getMessage(), id);
            throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(id));
        } else {
            log.info(LogMessages.TRAINEE_FOUND.getMessage(), id);
            return trainee.get();
        }
    }

    public TraineeServiceDTO getByUsername(String username) throws EntityNotFoundException {
        log.debug(LogMessages.RETRIEVING_TRAINEE_BY_USERNAME.getMessage(), username);

        Optional<TraineeEntity> trainee = traineeRepository.getByUserName(username);

        if (trainee.isEmpty()) {
            log.error(LogMessages.TRAINEE_NOT_FOUND_BY_USERNAME.getMessage(), username);
            throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND_BY_USERNAME.format(username));
        } else {
            log.info(LogMessages.TRAINEE_FOUND.getMessage(), trainee.get().getId());
            return TraineeConverter.toServiceDTO(trainee.get());
        }
    }

    public TraineeServiceDTO update(TraineeServiceDTO traineeDTO) throws EntityNotFoundException {
        log.debug(LogMessages.ATTEMPTING_TO_UPDATE_TRAINEE.getMessage(), traineeDTO.getId());

        TraineeEntity updatedTraineeEntity = TraineeConverter.toEntity(traineeDTO);
        try {
            //if (validateTrainee(traineeEntity))
            updatedTraineeEntity = traineeRepository.update(updatedTraineeEntity);
        } catch (EntityNotFoundException e) {
            log.warn(LogMessages.TRAINEE_NOT_FOUND.getMessage(), updatedTraineeEntity.getId());
            throw e;
        }

        log.info(LogMessages.UPDATED_TRAINEE.getMessage(), updatedTraineeEntity.getId());

        return TraineeConverter.toServiceDTO(updatedTraineeEntity);
    }

    public void deleteByUsername(String userName) {
        log.debug(LogMessages.ATTEMPTING_TO_DELETE_TRAINEE_BY_USERNAME.getMessage(), userName);

        traineeRepository.deleteByUserName(userName);
        log.info(LogMessages.DELETED_TRAINEE_BY_USERNAME.getMessage(), userName);
    }

    public boolean delete(TraineeEntity traineeEntity) {
        long id = traineeEntity.getId();
        log.debug(LogMessages.ATTEMPTING_TO_DELETE_TRAINEE.getMessage(), id);

        boolean deleted = traineeRepository.delete(traineeEntity);

        if (deleted)
            log.info(LogMessages.DELETED_TRAINEE.getMessage(), id);
        else
            log.warn(LogMessages.TRAINEE_NOT_FOUND.getMessage(), id);

        return deleted;
    }

    public boolean toggleActiveStatus(String username, boolean isActive) throws EntityNotFoundException {
        log.debug(LogMessages.ATTEMPTING_TO_CHANGE_TRAINEES_STATUS.getMessage(), username);

        boolean result;
        try {
            result = traineeRepository.toggleActiveStatus(username, isActive);
        } catch (EntityNotFoundException e) {
            log.warn(LogMessages.TRAINEE_NOT_FOUND_BY_USERNAME.getMessage(), username);
            throw e;
        }

        log.info(LogMessages.TRAINEES_STATUS_CHANGED.getMessage(), username, result);
        return result;
    }

    public List<TrainingByTraineeDTO> getTraineeTrainingsByCriteria(
            String traineeUserName, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType) {
        log.debug(LogMessages.FETCHING_TRAININGS_FOR_TRAINEE_BY_CRITERIA.getMessage(),
                traineeUserName, fromDate, toDate, trainerName, trainingType);

        List<TrainingEntity> trainings = traineeRepository.getTraineeTrainingsByCriteria(
                traineeUserName, fromDate, toDate, trainerName, trainingType);

        log.info(LogMessages.FOUND_TRAININGS_FOR_TRAINEE.getMessage(), trainings.size(), traineeUserName);
        return trainings.stream().map(TrainingConverter::toByTraineeDTO).collect(Collectors.toList());
    }

    public List<TrainerNestedDTO> getTrainersNotAssignedToTrainee(String traineeUserName) {
        log.info(LogMessages.FETCHING_TRAINERS_NOT_ASSIGNED_TO_TRAINEE.getMessage(), traineeUserName);

        List<TrainerEntity> trainers = traineeRepository.getTrainersNotAssignedToTrainee(traineeUserName);
        List<TrainerNestedDTO> trainerDTOs = trainers.stream().map(TrainerConverter::toNestedDTO).collect(Collectors.toList());
        log.info(LogMessages.FOUND_TRAINERS_NOT_ASSIGNED_TO_TRAINEE.getMessage(), trainers.size(), traineeUserName);

        return trainerDTOs;
    }


    @SneakyThrows
    public List<TrainerNestedDTO> updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames)  {
        TraineeEntity trainee = traineeRepository.getByUserName(traineeUsername)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND_BY_USERNAME.format(traineeUsername)));

        List<TrainerEntity> trainerEntities = trainerUsernames.stream()
                .map(trainerService::getByUsername)
                .map(TrainerConverter::toEntity)
                .collect(Collectors.toList());

        trainee.setTrainers(trainerEntities);
        return traineeRepository.updateTrainers(trainee).getTrainers().stream().map(TrainerConverter::toNestedDTO).collect(Collectors.toList());
    }


//    private boolean validateTrainee(TraineeEntity traineeEntity){
//        return traineeEntity.getId() != 0 && traineeEntity.getFirstName() != null && traineeEntity.getLastName() != null && traineeEntity.getUserName() != null && traineeEntity.getPassword() != null;
//    }
}
