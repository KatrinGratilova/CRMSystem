package org.example.crmsystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.UserEntity;
import org.example.crmsystem.exception.EntityNotFoundException;
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

    public boolean authenticate(String userName, String password) {
        UserEntity user = findUserByUsername(userName);

        if (user != null && user.getPassword().equals(password)) {
            authenticatedUsers.put(user.getId(), userName);
            log.info(LogMessages.USER_WAS_AUTHENTICATED.getMessage(), userName);
            return true;
        }
        log.warn(LogMessages.USER_DOES_NOT_EXIST.getMessage(), userName);
        return false;
    }

    public boolean isAuthenticated(String userName) {
        boolean isAuthenticated = authenticatedUsers.containsValue(userName);

        if (isAuthenticated)
            log.info(LogMessages.USER_IS_AUTHENTICATED_BY_USERNAME.getMessage(), userName);
        else
            log.warn(LogMessages.USER_IS_NOT_AUTHENTICATED_BY_USERNAME.getMessage(), userName);
        return isAuthenticated;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) throws EntityNotFoundException {
        UserEntity user = findUserByUsername(username);

        if (user != null) {
            log.debug(LogMessages.ATTEMPTING_TO_CHANGE_USER_PASSWORD.getMessage(), username);

            if (user.getPassword().equals(oldPassword)) {
                user.setPassword(newPassword);
                updateUser(user);
                log.info(LogMessages.USER_PASSWORD_CHANGED.getMessage(), username);
                return true;
            }
            log.warn(LogMessages.USER_PASSWORD_NOT_CHANGED.getMessage(), username);
        }
        throw new EntityNotFoundException(ExceptionMessages.USER_NOT_FOUND_BY_USERNAME.format(username));
    }

    private UserEntity findUserByUsername(String username) {
        Optional<TraineeEntity> trainee = traineeDAO.getByUserName(username);
        if (trainee.isPresent()) return trainee.get();
        return trainerDAO.getByUserName(username).orElse(null);
    }

    private void updateUser(UserEntity user) throws EntityNotFoundException {
        if (traineeDAO.getByUserName(user.getUserName()).isPresent())
            traineeDAO.updatePassword((TraineeEntity) user);
        else
            trainerDAO.updatePassword((TrainerEntity) user);
    }
}
