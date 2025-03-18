package org.example.crmsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.crmsystem.security.CustomUserDetailsService;
import org.example.crmsystem.security.JwtResponse;
import org.example.crmsystem.security.JwtTokenUtil;
import org.example.crmsystem.dto.trainee.TraineeServiceDTO;
import org.example.crmsystem.dto.user.UserChangeLoginRequestDTO;
import org.example.crmsystem.dto.user.UserCredentialsDTO;
import org.example.crmsystem.dto.user.UserUpdateStatusRequestDTO;
import org.example.crmsystem.exception.UserIsNotAuthenticated;
import org.example.crmsystem.service.AuthenticationService;
import org.example.crmsystem.service.TraineeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication Controller", description = "Operations related to user authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final TraineeService traineeService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService customUserDetailsService;

//    @GetMapping("/login")
//    @Operation(summary = "Login user by credentials", description = "Logins user by credentials")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully authenticated user"),
//            @ApiResponse(responseCode = "404", description = "User not found"),
//            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
//    })
//    public ResponseEntity<HttpStatus> login(@RequestBody UserCredentialsDTO user) {
//        boolean isAuthenticated = authenticationService.authenticate(user.getUsername(), user.getPassword());
//
//        if (isAuthenticated)
//            return new ResponseEntity<>(HttpStatus.ACCEPTED);
//        else
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody UserCredentialsDTO loginRequest) throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String jwt = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());

        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @PostMapping("/custom-logout")
    public ResponseEntity<?> customLogout(HttpServletRequest request, HttpServletResponse response) {
        // Получаем текущего аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(1);
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            TraineeServiceDTO user = traineeService.getByUsername(username);

            System.out.println(user);
            if (user != null) {
                user.setActive(false);
                boolean res = traineeService.toggleActiveStatus(username, new UserUpdateStatusRequestDTO(false)); // Сохраняем изменения
                System.out.println(res);
            }
        }

        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, authentication);

        return ResponseEntity.ok("Logout successful and user status updated");
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change user password", description = "Changed user password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully changed password"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<String> changePassword(@RequestBody UserChangeLoginRequestDTO user) throws EntityNotFoundException, UserIsNotAuthenticated {
        boolean isChanged = authenticationService.changePassword(user.getUsername(), user.getOldPassword(), user.getNewPassword());

        if (isChanged) {
            return ResponseEntity.ok("Password changed successfully");
        } else {
            return ResponseEntity.status(400).body("Old password is incorrect or user not found");
        }
    }
}
