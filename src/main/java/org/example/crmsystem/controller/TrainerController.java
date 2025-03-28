package org.example.crmsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.crmsystem.security.jwt.JwtTokenUtil;
import org.example.crmsystem.converter.TrainerConverter;
import org.example.crmsystem.dto.trainer.TrainerServiceDTO;
import org.example.crmsystem.dto.trainer.request.TrainerRegistrationRequestDTO;
import org.example.crmsystem.dto.trainer.request.TrainerUpdateRequestDTO;
import org.example.crmsystem.dto.trainer.response.TrainerGetResponseDTO;
import org.example.crmsystem.dto.trainer.response.TrainerUpdateResponseDTO;
import org.example.crmsystem.dto.training.TrainingByTrainerDTO;
import org.example.crmsystem.dto.user.UserRegisterResponseDTO;
import org.example.crmsystem.dto.user.UserUpdateStatusRequestDTO;
import org.example.crmsystem.service.TrainerService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/trainers", produces = {"application/JSON"})
@RequiredArgsConstructor
@Tag(name = "Trainer Controller", description = "Operations related to trainers")
public class TrainerController {
    private final TrainerService trainerService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping
    @Operation(summary = "Register a new trainer", description = "Creates a new trainer in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered a new trainer"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<UserRegisterResponseDTO> registerTrainer(@Valid @RequestBody TrainerRegistrationRequestDTO trainer) {
        TrainerServiceDTO trainerDTO = trainerService.createProfile(TrainerConverter.toServiceDTO(trainer));
        UserRegisterResponseDTO responseDTO = TrainerConverter.toUserRegistrationResponseDTO(trainerDTO);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(responseDTO.getUsername(), responseDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String jwt = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());
        responseDTO.setToken(jwt);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get trainer by username", description = "Retrieves trainer details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainer"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<TrainerGetResponseDTO> getTrainer(@PathVariable String username) throws EntityNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!currentUsername.equals(username))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        TrainerServiceDTO trainerDTO = trainerService.getByUsername(username);
        return new ResponseEntity<>(TrainerConverter.toGetResponseDTO(trainerDTO), HttpStatus.FOUND);
    }

    @PutMapping("/{username}")
    @Operation(summary = "Update trainer", description = "Updates trainer information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated trainer"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<TrainerUpdateResponseDTO> updateTrainer(@PathVariable("username") String username, @Valid @RequestBody TrainerUpdateRequestDTO trainer) throws EntityNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!currentUsername.equals(username))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        TrainerServiceDTO trainerDTO = trainerService.update(TrainerConverter.toServiceDTO(trainer));
        return new ResponseEntity<>(TrainerConverter.toUpdateResponseDTO(trainerDTO), HttpStatus.OK);
    }

    @PatchMapping("/{username}/status")
    @Operation(summary = "Update trainer active status by username", description = "Updates trainer status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated trainer status"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<HttpStatus> toggleActiveStatus(@PathVariable String username, @Valid @RequestBody UserUpdateStatusRequestDTO request) throws EntityNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!currentUsername.equals(username))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        trainerService.toggleActiveStatus(username, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{username}/trainings")
    @Operation(summary = "Get trainer trainings by username and criteria", description = "Retrieves trainer trainings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainer trainings by criteria"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<List<TrainingByTrainerDTO>> getTrainerTrainings(
            @PathVariable("username") String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(required = false) String traineeName
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!currentUsername.equals(username))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        List<TrainingByTrainerDTO> trainings = trainerService.getTrainerTrainingsByCriteria(username, fromDate, toDate, traineeName);
        return new ResponseEntity<>(trainings, HttpStatus.OK);
    }
}
