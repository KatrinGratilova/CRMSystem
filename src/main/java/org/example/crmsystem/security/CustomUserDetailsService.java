package org.example.crmsystem.security;

import lombok.RequiredArgsConstructor;
import org.example.crmsystem.dao.interfaces.TraineeRepositoryCustom;
import org.example.crmsystem.dao.interfaces.TrainerRepositoryCustom;
import org.example.crmsystem.entity.UserEntity;
import org.example.crmsystem.security.bruteForceProtecting.LoginAttemptService;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final TraineeRepositoryCustom traineeRepository;
    private final TrainerRepositoryCustom trainerRepository;
    private final LoginAttemptService loginAttemptService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (loginAttemptService.isBlocked(username))
            throw new LockedException("User is temporarily locked due to too many failed login attempts.");

        UserEntity user;
        if (traineeRepository.getByUsername(username).isPresent())
            user = traineeRepository.getByUsername(username).get();
        else
            user = trainerRepository.getByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .disabled(!user.isActive())
                .build();
    }
}

