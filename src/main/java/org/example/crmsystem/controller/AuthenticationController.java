package org.example.crmsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.crmsystem.dao.interfaces.RefreshTokenRepository;
import org.example.crmsystem.dto.user.UserChangeLoginRequestDTO;
import org.example.crmsystem.dto.user.UserCredentialsDTO;
import org.example.crmsystem.entity.RefreshToken;
import org.example.crmsystem.security.jwt.JwtResponse;
import org.example.crmsystem.security.jwt.JwtTokenUtil;
import org.example.crmsystem.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
@Tag(name = "Authentication Controller", description = "Operations related to user authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping()
    @Operation(summary = "Login user by credentials", description = "Logins user by credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<?> login(@RequestBody UserCredentialsDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        final String jwt = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());
//
//        return ResponseEntity.ok(new JwtResponse(jwt));

        final String accessToken = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());
        final String refreshToken = jwtTokenUtil.generateRefreshToken((UserDetails) authentication.getPrincipal());

        RefreshToken refresh = RefreshToken.builder()
                .token(refreshToken)
                .username(loginRequest.getUsername())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(10000000))
                .build();

        refreshTokenRepository.save(refresh);
        JwtResponse jwtResponse = new JwtResponse(accessToken, refreshToken);

        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/logout")
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
        boolean isChanged = authenticationService.changePassword(user.getUsername(), user.getOldPassword(), user.getNewPassword());

        if (isChanged) {
            return ResponseEntity.ok("Password changed successfully");
        } else {
            return ResponseEntity.status(400).body("Old password is incorrect or user not found");
        }
    }
}
