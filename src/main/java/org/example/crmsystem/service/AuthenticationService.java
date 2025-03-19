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
import org.example.crmsystem.exception.RefreshTokenDoesNotExist;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.messages.LogMessages;
import org.example.crmsystem.security.jwt.JwtResponse;
import org.example.crmsystem.security.jwt.JwtTokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final TraineeRepositoryCustom traineeRepository;
    private final TrainerRepositoryCustom trainerRepository;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final Map<Long, String> authenticatedUsers = new HashMap<>();

    @Transactional
    public JwtResponse login(Authentication authentication, String username) {
        final String accessToken = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());
        TokenWithMetadata refreshTokenData = jwtTokenUtil.generateRefreshToken((UserDetails) authentication.getPrincipal());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenData.token())
                .username(username)
                .createdAt(refreshTokenData.issuedAt())
                .expiresAt(refreshTokenData.expiration())
                .build();
        refreshTokenRepository.save(refreshToken);

        updateUserStatus(username);

        return new JwtResponse(accessToken, refreshToken.getToken());
    }

    @Transactional
    public ResponseEntity<Boolean> logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            String token = request.getHeader("Authorization");

            if (token != null && token.startsWith("Bearer "))
                token = token.substring(7);
            else
                throw new RefreshTokenDoesNotExist(ExceptionMessages.REFRESH_TOKEN_DOES_NOT_EXIST.getMessage());

            RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                    .orElseThrow(() -> new RefreshTokenDoesNotExist(ExceptionMessages.REFRESH_TOKEN_DOES_NOT_EXIST.getMessage()));

            refreshToken.setRefreshTokenStatus(RefreshTokenStatus.REVOKED);
            refreshTokenRepository.save(refreshToken);

            updateUserStatus(refreshToken.getUsername());

            return ResponseEntity.ok(true);
        } else {
            throw new RefreshTokenDoesNotExist(ExceptionMessages.REFRESH_TOKEN_DOES_NOT_EXIST.getMessage());
        }
    }

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

    private void updateUserStatus(String username) {
        UserEntity user;

        if (traineeRepository.getByUsername(username).isPresent())
            user = traineeRepository.getByUsername(username).get();
        else
            user = trainerRepository.getByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.USER_NOT_FOUND.format(username)));

        user.setActive(false);
        updateUser(user);
    }
}
