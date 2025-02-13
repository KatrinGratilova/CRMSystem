package org.example.crmsystem.service;

import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.exception.UserIsNotAuthenticated;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.messages.LogMessages;
import org.example.crmsystem.utils.PasswordGenerator;
import org.example.crmsystem.utils.UsernameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class TraineeService {
    private final TraineeDAO traineeRepository;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;
    private final AuthenticationService authenticationService;

    @Autowired
    public TraineeService(TraineeDAO traineeRepository, PasswordGenerator passwordGenerator, UsernameGenerator usernameGenerator, AuthenticationService authenticationService) {
        this.traineeRepository = traineeRepository;
        this.passwordGenerator = passwordGenerator;
        this.usernameGenerator = usernameGenerator;
        this.authenticationService = authenticationService;
    }

    public TraineeEntity createProfile(TraineeEntity traineeEntity) {
        log.debug(LogMessages.ATTEMPTING_TO_ADD_NEW_TRAINEE.getMessage(), traineeEntity.getFirstName());

        traineeEntity.setPassword(passwordGenerator.generateUserPassword());
        traineeEntity.setUserName(usernameGenerator.generateUserName(traineeEntity));

        TraineeEntity addedTraineeEntity = traineeRepository.add(traineeEntity);
        authenticationService.authenticate(traineeEntity.getUserName(), traineeEntity.getPassword());

        log.info(LogMessages.ADDED_NEW_TRAINEE.getMessage(), addedTraineeEntity.getId());
        return addedTraineeEntity;
    }

    public TraineeEntity getById(long id) throws EntityNotFoundException, UserIsNotAuthenticated {
        log.debug(LogMessages.RETRIEVING_TRAINEE.getMessage(), id);

        if (authenticationService.isAuthenticated(id)) {
            Optional<TraineeEntity> trainee = traineeRepository.getById(id);

            if (trainee.isEmpty()) {
                log.error(LogMessages.TRAINEE_NOT_FOUND.getMessage(), id);
                throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(id));
            } else {
                log.info(LogMessages.TRAINEE_FOUND.getMessage(), id);
                return trainee.get();
            }
        }
        throw new UserIsNotAuthenticated(ExceptionMessages.USER_IS_NOT_AUTHENTICATED.format(id));
    }

    public TraineeEntity getByUsername(String username) throws EntityNotFoundException, UserIsNotAuthenticated {
        log.debug(LogMessages.RETRIEVING_TRAINEE_BY_USERNAME.getMessage(), username);

        if (authenticationService.isAuthenticated(username)) {
            Optional<TraineeEntity> trainee = traineeRepository.getByUserName(username);

            if (trainee.isEmpty()) {
                log.error(LogMessages.TRAINEE_NOT_FOUND_BY_USERNAME.getMessage(), username);
                throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND_BY_USERNAME.format(username));
            } else {
                log.info(LogMessages.TRAINEE_FOUND.getMessage(), trainee.get().getId());
                return trainee.get();
            }
        }
        throw new UserIsNotAuthenticated(ExceptionMessages.USER_IS_NOT_AUTHENTICATED_WITH_USERNAME.format(username));
    }

    public TraineeEntity update(TraineeEntity traineeEntity) throws UserIsNotAuthenticated, EntityNotFoundException {
        log.debug(LogMessages.ATTEMPTING_TO_UPDATE_TRAINEE.getMessage(), traineeEntity.getId());

        if (authenticationService.isAuthenticated(traineeEntity.getId())) {
            TraineeEntity updatedTraineeEntity;
            try {
                updatedTraineeEntity = traineeRepository.update(traineeEntity);
            } catch (EntityNotFoundException e) {
                log.warn(LogMessages.TRAINEE_NOT_FOUND.getMessage(), traineeEntity.getId());
                throw e;
            }

            log.info(LogMessages.UPDATED_TRAINEE.getMessage(), updatedTraineeEntity.getId());
            return updatedTraineeEntity;
        }
        throw new UserIsNotAuthenticated(ExceptionMessages.USER_IS_NOT_AUTHENTICATED.format(traineeEntity.getId()));
    }

    public void deleteByUsername(String userName) throws UserIsNotAuthenticated {
        log.debug(LogMessages.ATTEMPTING_TO_DELETE_TRAINEE_BY_USERNAME.getMessage(), userName);

        if (authenticationService.isAuthenticated(userName)) {
            traineeRepository.deleteByUserName(userName);
            log.info(LogMessages.DELETED_TRAINEE_BY_USERNAME.getMessage(), userName);
        } else {
            throw new UserIsNotAuthenticated(ExceptionMessages.USER_IS_NOT_AUTHENTICATED_WITH_USERNAME.format(userName));
        }
    }

    public boolean delete(TraineeEntity traineeEntity) throws UserIsNotAuthenticated {
        long id = traineeEntity.getId();
        log.debug(LogMessages.ATTEMPTING_TO_DELETE_TRAINEE.getMessage(), id);

        if (authenticationService.isAuthenticated(id)) {
            boolean deleted = traineeRepository.delete(traineeEntity);

            if (deleted)
                log.info(LogMessages.DELETED_TRAINEE.getMessage(), id);
            else
                log.warn(LogMessages.TRAINEE_NOT_FOUND.getMessage(), id);

            return deleted;
        }
        throw new UserIsNotAuthenticated(ExceptionMessages.USER_IS_NOT_AUTHENTICATED.format(id));
    }

    public boolean toggleActiveStatus(TraineeEntity traineeEntity) throws UserIsNotAuthenticated, EntityNotFoundException {
        long id = traineeEntity.getId();
        log.debug(LogMessages.ATTEMPTING_TO_CHANGE_TRAINEES_STATUS.getMessage(), id);

        if (authenticationService.isAuthenticated(id)) {
            boolean result;
            try {
                result = traineeRepository.toggleActiveStatus(traineeEntity);
            } catch (EntityNotFoundException e) {
                log.warn(LogMessages.TRAINEE_NOT_FOUND.getMessage(), traineeEntity.getId());
                throw e;
            }

            log.info(LogMessages.TRAINEES_STATUS_CHANGED.getMessage(), id, result);
            return result;
        }
        throw new UserIsNotAuthenticated(ExceptionMessages.USER_IS_NOT_AUTHENTICATED.format(id));
    }

    public boolean changePassword(String userName, String oldPassword, String newPassword) throws EntityNotFoundException, UserIsNotAuthenticated {
        TraineeEntity traineeEntity = traineeRepository.getByUserName(userName)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.TRAINEE_WITH_USERNAME_IS_NOT_FOUND.format(userName)));

        long id = traineeEntity.getId();
        log.debug(LogMessages.ATTEMPTING_TO_CHANGE_TRAINEES_PASSWORD.getMessage(), id);

        if (authenticationService.isAuthenticated(traineeEntity.getId())) {
            if (traineeEntity.getPassword().equals(oldPassword)) {
                traineeEntity.setPassword(newPassword);

                traineeRepository.update(traineeEntity);
                log.info(LogMessages.TRAINEES_PASSWORD_CHANGED.getMessage(), id);
                return true;
            }
            log.warn(LogMessages.TRAINEES_PASSWORD_NOT_CHANGED.getMessage(), id);
            return false;
        }

        throw new UserIsNotAuthenticated(ExceptionMessages.USER_IS_NOT_AUTHENTICATED.format(id));
    }

    public List<TrainingEntity> getTraineeTrainingsByCriteria(
            String traineeUserName, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType) throws UserIsNotAuthenticated {
        log.debug(LogMessages.FETCHING_TRAININGS_FOR_TRAINEE_BY_CRITERIA.getMessage(),
                traineeUserName, fromDate, toDate, trainerName, trainingType);

        if (authenticationService.isAuthenticated(traineeUserName)) {
            List<TrainingEntity> trainings = traineeRepository.getTraineeTrainingsByCriteria(
                    traineeUserName, fromDate, toDate, trainerName, trainingType);

            log.info(LogMessages.FOUND_TRAININGS_FOR_TRAINEE.getMessage(), trainings.size(), traineeUserName);
            return trainings;
        }
        throw new UserIsNotAuthenticated(ExceptionMessages.USER_IS_NOT_AUTHENTICATED_WITH_USERNAME.format(traineeUserName));
    }
}
