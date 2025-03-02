package org.example.crmsystem.controller;

import lombok.RequiredArgsConstructor;
import org.example.crmsystem.dto.user.UserChangeLoginDTO;
import org.example.crmsystem.dto.user.UserCredentialsDTO;
import jakarta.persistence.EntityNotFoundException;
import org.example.crmsystem.exception.UserIsNotAuthenticated;
import org.example.crmsystem.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<HttpStatus> login(@RequestBody UserCredentialsDTO user) {
        boolean isAuthenticated = authenticationService.authenticate(user.getUsername(), user.getPassword());

        if (isAuthenticated)
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        else
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody UserChangeLoginDTO user) throws EntityNotFoundException, UserIsNotAuthenticated {
        boolean isChanged = authenticationService.changePassword(user.getUsername(), user.getOldPassword(), user.getNewPassword());

        if (isChanged) {
            return ResponseEntity.ok("Password changed successfully");
        } else {
            return ResponseEntity.status(400).body("Old password is incorrect or user not found");
        }
    }
}
