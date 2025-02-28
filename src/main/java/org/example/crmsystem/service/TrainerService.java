package org.example.crmsystem.service;

import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.converter.TrainerConverter;
import org.example.crmsystem.converter.TrainingConverter;
import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.dto.trainer.TrainerServiceDTO;
import org.example.crmsystem.dto.training.TrainingByTrainerDTO;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.messages.LogMessages;
import org.example.crmsystem.utils.PasswordGenerator;
import org.example.crmsystem.utils.UsernameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public TrainerServiceDTO createProfile(TrainerServiceDTO trainerDTO) {
        log.debug(LogMessages.ADDED_NEW_TRAINER.getMessage(), trainerDTO.getFirstName());

        trainerDTO.setUserName(usernameGenerator.generateUserName(trainerDTO));
        trainerDTO.setPassword(passwordGenerator.generateUserPassword());

        TrainerEntity addedTrainerEntity = trainerRepository.add(TrainerConverter.toEntity(trainerDTO));
        authenticationService.authenticate(trainerDTO.getUserName(), trainerDTO.getPassword());

        log.info(LogMessages.ADDED_NEW_TRAINER.getMessage(), addedTrainerEntity.getId());
        return TrainerConverter.toServiceDTO(addedTrainerEntity);
    }

    public TrainerEntity getById(long id) throws EntityNotFoundException {
        log.debug(LogMessages.RETRIEVING_TRAINER.getMessage(), id);

        Optional<TrainerEntity> trainer = trainerRepository.getById(id);

        if (trainer.isEmpty()) {
            log.error(LogMessages.TRAINER_NOT_FOUND.getMessage(), id);
            throw new EntityNotFoundException(ExceptionMessages.TRAINER_NOT_FOUND.format(id));
        } else {
            log.info(LogMessages.TRAINER_FOUND.getMessage(), id);
            return trainer.get();
        }

    }

    public TrainerServiceDTO getByUsername(String username) throws jakarta.persistence.EntityNotFoundException {
        log.debug(LogMessages.RETRIEVING_TRAINER_BY_USERNAME.getMessage(), username);

        Optional<TrainerEntity> trainer = trainerRepository.getByUserName(username);

        if (trainer.isEmpty()) {
            log.error(LogMessages.TRAINER_NOT_FOUND_BY_USERNAME.getMessage(), username);
            throw new jakarta.persistence.EntityNotFoundException(ExceptionMessages.TRAINER_NOT_FOUND_BY_USERNAME.format(username));
        } else {
            log.info(LogMessages.TRAINER_FOUND.getMessage(), trainer.get().getId());
            System.out.println(TrainerConverter.toServiceDTO(trainer.get()));
            return TrainerConverter.toServiceDTO(trainer.get());
        }
    }

    public TrainerServiceDTO update(TrainerServiceDTO trainerDTO) throws EntityNotFoundException {
        log.debug(LogMessages.ATTEMPTING_TO_UPDATE_TRAINER.getMessage(), trainerDTO.getId());

        TrainerEntity updatedTrainerEntity = TrainerConverter.toEntity(trainerDTO);
        try {
            //   if (validateTrainer(trainerEntity))
            updatedTrainerEntity = trainerRepository.update(updatedTrainerEntity);
        } catch (EntityNotFoundException e) {
            log.warn(LogMessages.TRAINER_NOT_FOUND.getMessage(), updatedTrainerEntity.getId());
            throw e;
        }

        log.info(LogMessages.UPDATED_TRAINER.getMessage(), updatedTrainerEntity.getId());
        return TrainerConverter.toServiceDTO(updatedTrainerEntity);

    }

    public boolean toggleActiveStatus(String username, boolean isActive) throws EntityNotFoundException {
        log.debug(LogMessages.ATTEMPTING_TO_CHANGE_TRAINERS_STATUS.getMessage(), username);

        boolean result;
        try {
            result = trainerRepository.toggleActiveStatus(username, isActive);
        } catch (EntityNotFoundException e) {
            log.warn(LogMessages.TRAINER_NOT_FOUND_BY_USERNAME.getMessage(), username);
            throw e;
        }

        log.info(LogMessages.TRAINERS_STATUS_CHANGED.getMessage(), username, result);
        return result;
    }

//    public boolean changePassword(String userName, String oldPassword, String newPassword) throws EntityNotFoundException {
//        TrainerEntity trainerEntity = trainerRepository.getByUserName(userName)
//                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.TRAINER_WITH_USERNAME_IS_NOT_FOUND.format(userName)));
//
//        long id = trainerEntity.getId();
//        log.debug(LogMessages.ATTEMPTING_TO_CHANGE_TRAINERS_PASSWORD.getMessage(), id);
//
//
//        if (trainerEntity.getPassword().equals(oldPassword)) {
//            trainerEntity.setPassword(newPassword);
//
//            //if (validateTrainer(trainerEntity)) {
//            trainerRepository.update(trainerEntity, "");
//            log.info(LogMessages.TRAINERS_PASSWORD_CHANGED.getMessage(), trainerEntity.getId());
//            return true;
//            // }
//            // return false;
//        }
//        log.warn(LogMessages.TRAINERS_PASSWORD_NOT_CHANGED);
//        return false;
//
//    }

    public List<TrainingByTrainerDTO> getTrainerTrainingsByCriteria(
            String trainerUserName, LocalDateTime fromDate, LocalDateTime toDate, String traineeName) {
        log.debug(LogMessages.FETCHING_TRAININGS_FOR_TRAINER_BY_CRITERIA.getMessage(),
                trainerUserName, fromDate, toDate, traineeName);

        List<TrainingEntity> trainings = trainerRepository.getTrainerTrainingsByCriteria(
                trainerUserName, fromDate, toDate, traineeName);

        log.info(LogMessages.FOUND_TRAININGS_FOR_TRAINER.getMessage(), trainings.size(), trainerUserName);

        return trainings.stream().map(TrainingConverter::toByTrainerDTO).collect(Collectors.toList());

    }

//    private boolean validateTrainer(TrainerEntity trainerEntity){
//        return trainerEntity.getId() != 0 && trainerEntity.getFirstName() != null && trainerEntity.getLastName() != null && trainerEntity.getUserName() != null && trainerEntity.getPassword() != null && trainerEntity.getSpecialization() != null;
//    }
}
