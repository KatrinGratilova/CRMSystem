package org.example.crmsystem.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.UserEntity;
import org.example.crmsystem.exception.UserIsNotAuthenticated;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.messages.LogMessages;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class AuthenticationService {
    private final TraineeDAO traineeDAO;
    private final TrainerDAO trainerDAO;
    private final Map<Long, String> authenticatedUsers = new HashMap<>();

    public boolean authenticate(String username, String password) {
        String transactionId = ThreadContext.get("transactionId");
        UserEntity user = findUserByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            authenticatedUsers.put(user.getId(), username);
            log.info(LogMessages.USER_WAS_AUTHENTICATED.getMessage(), transactionId, username);
            return true;
        }
        log.warn(LogMessages.USER_DOES_NOT_EXIST.getMessage(), transactionId, username);
        return false;
    }

    public boolean isAuthenticated(String username) {
        String transactionId = ThreadContext.get("transactionId");
        boolean isAuthenticated = authenticatedUsers.containsValue(username);

        if (isAuthenticated)
            log.info(LogMessages.USER_IS_AUTHENTICATED.getMessage(), transactionId, username);
        else {
            if (traineeDAO.getByUsername(username).isPresent())
                log.warn(LogMessages.USER_IS_NOT_AUTHENTICATED.getMessage(), transactionId, username);
            else {
                log.warn(LogMessages.USER_DOES_NOT_EXIST.getMessage(), transactionId, username);
                throw new EntityNotFoundException(ExceptionMessages.USER_NOT_FOUND.format(username));
            }
        }
        return isAuthenticated;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) throws EntityNotFoundException, UserIsNotAuthenticated {
        String transactionId = ThreadContext.get("transactionId");

        if (isAuthenticated(username)) {
            UserEntity user = findUserByUsername(username);

            if (user != null) {
                log.debug(LogMessages.ATTEMPTING_TO_CHANGE_USER_PASSWORD.getMessage(), transactionId, username);

                if (user.getPassword().equals(oldPassword)) {
                    user.setPassword(newPassword);
                    updateUser(user);
                    log.info(LogMessages.USER_PASSWORD_CHANGED.getMessage(), transactionId, username);
                    return true;
                }
                log.warn(LogMessages.USER_PASSWORD_NOT_CHANGED.getMessage(), transactionId, username);
                return false;
            }
            throw new EntityNotFoundException(ExceptionMessages.USER_NOT_FOUND.format(username));
        }
        return false;
    }

    private UserEntity findUserByUsername(String username) {
        Optional<TraineeEntity> trainee = traineeDAO.getByUsername(username);
        if (trainee.isPresent()) return trainee.get();
        return trainerDAO.getByUsername(username).orElse(null);
    }

    private void updateUser(UserEntity user) throws EntityNotFoundException {
        if (traineeDAO.getByUsername(user.getUsername()).isPresent())
            traineeDAO.updatePassword((TraineeEntity) user);
        else
            trainerDAO.updatePassword((TrainerEntity) user);
    }
}
