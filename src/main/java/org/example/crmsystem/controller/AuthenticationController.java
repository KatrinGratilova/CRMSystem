package org.example.crmsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.crmsystem.dto.user.UserChangeLoginRequestDTO;
import org.example.crmsystem.dto.user.UserCredentialsDTO;
import org.example.crmsystem.security.jwt.JwtResponse;
import org.example.crmsystem.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
@Tag(name = "Authentication Controller", description = "Operations related to user authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;

    @PostMapping()
    @Operation(summary = "Login user by credentials", description = "Logins user by credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated user"),
            @ApiResponse(responseCode = "403", description = "Bad credentials"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<?> login(@RequestBody UserCredentialsDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        JwtResponse jwtResponse = authenticationService.login(authentication, loginRequest.getUsername());

        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logouts user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout user successfully"),
            @ApiResponse(responseCode = "403", description = "Refresh token does not exist"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<Boolean> logout(HttpServletRequest request,
                                                       HttpServletResponse response) throws IOException {
        return authenticationService.logout(request, response);
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change user password", description = "Changed user password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully changed password"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<String> changePassword(@RequestBody UserChangeLoginRequestDTO user) throws EntityNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!currentUsername.equals(user.getUsername()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        boolean isChanged = authenticationService.changePassword(user.getUsername(), user.getOldPassword(), user.getNewPassword());

        if (isChanged) {
            return ResponseEntity.status(200).body("Changed password successfully");
        } else {
            return ResponseEntity.status(400).body("Old password is incorrect or user not found");
        }
    }
}
