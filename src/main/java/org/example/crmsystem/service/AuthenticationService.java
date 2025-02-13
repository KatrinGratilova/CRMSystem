package org.example.crmsystem.service;

import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.dao.interfaces.HavingUserName;
import org.example.crmsystem.entity.UserEntity;
import org.example.crmsystem.messages.LogMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Log4j2
public class AuthenticationService {
    private final List<HavingUserName<? extends UserEntity>> userRepositories;
    private final Map<Long, String> authenticatedUsers = new HashMap<>();

    @Autowired
    public AuthenticationService(List<HavingUserName<? extends UserEntity>> userRepositories) {
        this.userRepositories = userRepositories;
    }

    public boolean authenticate(String userName, String password) {
        for (HavingUserName<? extends UserEntity> repository : userRepositories) {
            Optional<? extends UserEntity> optionalUser = repository.getByUserName(userName);
            if (optionalUser.isPresent()) {
                UserEntity user = optionalUser.get();
                if (user.getPassword().equals(password)) {
                    authenticatedUsers.put(user.getId(), userName);
                    log.info(LogMessages.USER_WAS_AUTHENTICATED.getMessage(), userName);
                    return true;
                }
            }
        }

        log.warn(LogMessages.USER_DOES_NOT_EXIST.getMessage(), userName);
        return false;
    }

    public boolean isAuthenticated(long userId) {
        boolean isAuthenticated = authenticatedUsers.containsKey(userId);
        if (isAuthenticated)
            log.info(LogMessages.USER_IS_AUTHENTICATED.getMessage(), userId);
        else
            log.warn(LogMessages.USER_IS_NOT_AUTHENTICATED.getMessage(), userId);

        return isAuthenticated;
    }

    public boolean isAuthenticated(String userName) {
        boolean isAuthenticated = authenticatedUsers.containsValue(userName);

        if (isAuthenticated)
            log.info(LogMessages.USER_IS_AUTHENTICATED_BY_USERNAME.getMessage(), userName);
        else
            log.warn(LogMessages.USER_IS_NOT_AUTHENTICATED_BY_USERNAME.getMessage(), userName);

        return isAuthenticated;
    }
}
