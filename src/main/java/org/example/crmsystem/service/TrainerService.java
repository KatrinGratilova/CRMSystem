package org.example.crmsystem.service;

import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.entity.TrainerEntity;
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
public class TrainerService {
    private final TrainerDAO trainerRepository;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;
    private final AuthenticationService authenticationService;

    @Autowired
    public TrainerService(TrainerDAO trainerRepository, PasswordGenerator passwordGenerator, UsernameGenerator usernameGenerator, AuthenticationService authenticationService) {
        this.trainerRepository = trainerRepository;
        this.passwordGenerator = passwordGenerator;
        this.usernameGenerator = usernameGenerator;
        this.authenticationService = authenticationService;
    }

    public TrainerEntity createProfile(TrainerEntity trainerEntity) {
        log.debug(LogMessages.ADDED_NEW_TRAINER.getMessage(), trainerEntity.getFirstName());

        trainerEntity.setUserName(usernameGenerator.generateUserName(trainerEntity));
        trainerEntity.setPassword(passwordGenerator.generateUserPassword());

        TrainerEntity addedTrainerEntity = trainerRepository.add(trainerEntity);
        authenticationService.authenticate(trainerEntity.getUserName(), trainerEntity.getPassword());

        log.info(LogMessages.ADDED_NEW_TRAINER.getMessage(), addedTrainerEntity.getId());
        return addedTrainerEntity;
    }

    public TrainerEntity getById(long id) throws EntityNotFoundException, UserIsNotAuthenticated {
        log.debug(LogMessages.RETRIEVING_TRAINER.getMessage(), id);

        if (authenticationService.isAuthenticated(id)) {
            Optional<TrainerEntity> trainer = trainerRepository.getById(id);

            if (trainer.isEmpty()) {
                log.error(LogMessages.TRAINER_NOT_FOUND.getMessage(), id);
                throw new EntityNotFoundException(ExceptionMessages.TRAINER_NOT_FOUND.format(id));
            } else {
                log.info(LogMessages.TRAINER_FOUND.getMessage(), id);
                return trainer.get();
            }
        }
        throw new UserIsNotAuthenticated(ExceptionMessages.USER_IS_NOT_AUTHENTICATED.format(id));
    }

    public TrainerEntity getByUsername(String username) throws EntityNotFoundException, UserIsNotAuthenticated {
        log.debug(LogMessages.RETRIEVING_TRAINER_BY_USERNAME.getMessage(), username);

        if (authenticationService.isAuthenticated(username)) {
            Optional<TrainerEntity> trainer = trainerRepository.getByUserName(username);

            if (trainer.isEmpty()) {
                log.error(LogMessages.TRAINER_NOT_FOUND_BY_USERNAME.getMessage(), username);
                throw new EntityNotFoundException(ExceptionMessages.TRAINER_NOT_FOUND_BY_USERNAME.format(username));
            } else {
                log.info(LogMessages.TRAINER_FOUND.getMessage(), trainer.get().getId());
                return trainer.get();
            }
        }
        throw new UserIsNotAuthenticated(ExceptionMessages.USER_IS_NOT_AUTHENTICATED_WITH_USERNAME.format(username));
    }

    public TrainerEntity update(TrainerEntity trainerEntity) throws EntityNotFoundException, UserIsNotAuthenticated {
        log.debug(LogMessages.ATTEMPTING_TO_UPDATE_TRAINER.getMessage(), trainerEntity.getId());

        if (authenticationService.isAuthenticated(trainerEntity.getId())) {
            TrainerEntity updatedTrainerEntity;
            try {
                updatedTrainerEntity = trainerRepository.update(trainerEntity);
            } catch (EntityNotFoundException e) {
                log.warn(LogMessages.TRAINER_NOT_FOUND.getMessage(), trainerEntity.getId());
                throw e;
            }

            log.info(LogMessages.UPDATED_TRAINER.getMessage(), updatedTrainerEntity.getId());
            return updatedTrainerEntity;
        }
        throw new UserIsNotAuthenticated(ExceptionMessages.USER_IS_NOT_AUTHENTICATED.format(trainerEntity.getId()));

    }

    public boolean toggleActiveStatus(TrainerEntity trainerEntity) throws UserIsNotAuthenticated, EntityNotFoundException {
        long id = trainerEntity.getId();
        log.debug(LogMessages.ATTEMPTING_TO_CHANGE_TRAINERS_STATUS.getMessage(), id);

        if (authenticationService.isAuthenticated(id)) {
            boolean result;
            try {
                result = trainerRepository.toggleActiveStatus(trainerEntity);
            } catch (EntityNotFoundException e) {
                log.warn(LogMessages.TRAINER_NOT_FOUND.getMessage(), trainerEntity.getId());
                throw e;
            }

            log.info(LogMessages.TRAINERS_STATUS_CHANGED.getMessage(), id, result);
            return result;
        }
        throw new UserIsNotAuthenticated(ExceptionMessages.USER_IS_NOT_AUTHENTICATED.format(id));
    }

    public List<TrainerEntity> getTrainersNotAssignedToTrainee(String traineeUserName) throws UserIsNotAuthenticated {
        log.info(LogMessages.FETCHING_TRAINERS_NOT_ASSIGNED_TO_TRAINEE.getMessage(), traineeUserName);

        if (authenticationService.isAuthenticated(traineeUserName)) {
            List<TrainerEntity> trainers = trainerRepository.getTrainersNotAssignedToTrainee(traineeUserName);
            log.info(LogMessages.FOUND_TRAINERS_NOT_ASSIGNED_TO_TRAINEE.getMessage(), trainers.size(), traineeUserName);

            return trainers;
        }
        throw new UserIsNotAuthenticated(ExceptionMessages.USER_IS_NOT_AUTHENTICATED_WITH_USERNAME.format(traineeUserName));
    }

    public boolean changePassword(String userName, String oldPassword, String newPassword) throws EntityNotFoundException, UserIsNotAuthenticated {
        TrainerEntity trainerEntity = trainerRepository.getByUserName(userName)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.TRAINER_WITH_USERNAME_IS_NOT_FOUND.format(userName)));

        long id = trainerEntity.getId();
        log.debug(LogMessages.ATTEMPTING_TO_CHANGE_TRAINERS_PASSWORD.getMessage(), id);

        if (authenticationService.isAuthenticated(trainerEntity.getId())) {
            if (trainerEntity.getPassword().equals(oldPassword)) {
                trainerEntity.setPassword(newPassword);

                trainerRepository.update(trainerEntity);
                log.info(LogMessages.TRAINERS_PASSWORD_CHANGED.getMessage(), trainerEntity.getId());
                return true;
            }
            log.warn(LogMessages.TRAINERS_PASSWORD_NOT_CHANGED);
            return false;
        }
        throw new UserIsNotAuthenticated(ExceptionMessages.USER_IS_NOT_AUTHENTICATED.format(id));
    }

    public List<TrainingEntity> getTrainerTrainingsByCriteria(
            String trainerUserName, LocalDateTime fromDate, LocalDateTime toDate, String traineeName) throws UserIsNotAuthenticated {
        log.debug(LogMessages.FETCHING_TRAININGS_FOR_TRAINER_BY_CRITERIA.getMessage(),
                trainerUserName, fromDate, toDate, traineeName);

        if (authenticationService.isAuthenticated(trainerUserName)) {
            List<TrainingEntity> trainings = trainerRepository.getTrainerTrainingsByCriteria(
                    trainerUserName, fromDate, toDate, traineeName);

            log.info(LogMessages.FOUND_TRAININGS_FOR_TRAINER.getMessage(), trainings.size(), trainerUserName);
            return trainings;
        }
        throw new UserIsNotAuthenticated(ExceptionMessages.USER_IS_NOT_AUTHENTICATED_WITH_USERNAME.format(trainerUserName));
    }
}
