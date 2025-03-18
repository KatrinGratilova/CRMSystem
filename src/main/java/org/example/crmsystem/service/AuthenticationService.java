package org.example.crmsystem.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.example.crmsystem.dao.interfaces.RefreshTokenRepository;
import org.example.crmsystem.dao.interfaces.TraineeRepositoryCustom;
import org.example.crmsystem.dao.interfaces.TrainerRepositoryCustom;
import org.example.crmsystem.entity.*;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.messages.LogMessages;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class AuthenticationService {
    private final TraineeRepositoryCustom traineeDAO;
    private final TrainerRepositoryCustom trainerDAO;
    private final RefreshTokenRepository refreshTokenRepository;
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
            if (traineeDAO.getByUsername(username).isPresent() || trainerDAO.getByUsername(username).isPresent())
                log.warn(LogMessages.USER_IS_NOT_AUTHENTICATED.getMessage(), transactionId, username);
            else {
                log.warn(LogMessages.USER_DOES_NOT_EXIST.getMessage(), transactionId, username);
                throw new EntityNotFoundException(ExceptionMessages.USER_NOT_FOUND.format(username));
            }
        }
        return isAuthenticated;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) throws EntityNotFoundException {
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



    @Transactional
    // Ensures that the logout process, including any database changes, is handled within a single transaction
    public ResponseEntity<Boolean> logout(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Retrieves the current authentication information from the SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        System.out.println(2);
        // Checks if the user is authenticated (authentication is not null and authenticated)
        if (authentication != null && authentication.isAuthenticated()) {

            System.out.println(3);
            // Logs out the user by clearing their authentication info from the SecurityContext
            new SecurityContextLogoutHandler().logout(request, response, authentication);

            // Gets the Authorization header from the request to retrieve the JWT token
            String token = request.getHeader("Authorization");

            // Checks if the token is in "Bearer <token>" format, then removes "Bearer " prefix to extract only the token value
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // Extracts the actual token by removing the "Bearer " prefix
            } else {
                // If no valid token is provided, throws a custom exception indicating the refresh token does not exist
                throw new IOException("REFRESH_TOKEN_DOES_NOT_EXIST");
            }

            // Searches the database for the provided token in the refreshTokenRepository
            RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                    // If no matching token is found, throws an exception for a non-existing refresh token
                    .orElseThrow(() -> new IOException("REFRESH_TOKEN_DOES_NOT_EXIST"));

            // Sets the status of the found refresh token to REVOKED, marking it as unusable
            refreshToken.setRefreshTokenStatus(RefreshTokenStatus.REVOKED);

            // Saves the updated refresh token back to the database to persist the status change
            refreshTokenRepository.save(refreshToken);

            // Returns a successful response indicating that the logout process was completed
            return ResponseEntity.ok(true);
        } else {
            // Throws an exception if the user was not authenticated in the first place
            throw new IOException("REFRESH_TOKEN_DOES_NOT_EXIST");
        }
    }
}
