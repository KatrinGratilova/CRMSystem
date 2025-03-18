package org.example.crmsystem.configuration;

import lombok.RequiredArgsConstructor;
import org.example.crmsystem.dao.interfaces.TraineeRepositoryCustom;
import org.example.crmsystem.entity.UserEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final TraineeRepositoryCustom userRepository;
    private final org.example.crmsystem.security.LoginAttemptService loginAttemptService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (loginAttemptService.isBlocked(username)) {
            throw new LockedException("User is temporarily locked due to too many failed login attempts.");
        }

        UserEntity user = userRepository.getByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        System.out.println(user);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER") // или подтягивай роли из базы
                .disabled(!user.isActive()) // теперь зависит от isActive
                .build();
    }

//    @Autowired
//    private UserRepository userRepository;

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        UserEntity employee = userRepository.findByName(username);
//
//        Set<GrantedAuthority> authorities = employee.getRoles().stream()
//                .map((roles) -> new SimpleGrantedAuthority(roles.toString()))
//                .collect(Collectors.toSet());
//
//        return new org.springframework.security.core.userdetails.User(
//                username,
//                employee.getPassword(),
//                authorities
//        );
//    }
}

