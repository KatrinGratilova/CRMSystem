package org.example.crmsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.crmsystem.converter.TrainerConverter;
import org.example.crmsystem.dto.trainer.TrainerServiceDTO;
import org.example.crmsystem.dto.trainer.request.TrainerRegistrationRequestDTO;
import org.example.crmsystem.dto.trainer.request.TrainerUpdateRequestDTO;
import org.example.crmsystem.dto.trainer.response.TrainerGetResponseDTO;
import org.example.crmsystem.dto.trainer.response.TrainerUpdateResponseDTO;
import org.example.crmsystem.dto.training.TrainingByTrainerDTO;
import org.example.crmsystem.dto.user.UserCredentialsDTO;
import org.example.crmsystem.dto.user.UserUpdateStatusRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import org.example.crmsystem.service.TrainerService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/trainers", produces = {"application/JSON"})
@RequiredArgsConstructor
@Tag(name = "Trainer Controller", description = "Operations related to trainers")
public class TrainerController {
    private final TrainerService trainerService;

    @PostMapping
    @Operation(summary = "Register a new trainer", description = "Creates a new trainer in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered a new trainer"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<UserCredentialsDTO> registerTrainer(@Valid @RequestBody TrainerRegistrationRequestDTO trainer) {
        TrainerServiceDTO trainerDTO = trainerService.createProfile(TrainerConverter.toServiceDTO(trainer));
        return new ResponseEntity<>(TrainerConverter.toRegistrationResponseDTO(trainerDTO), HttpStatus.CREATED);
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
        TrainerServiceDTO trainerDTO = trainerService.getByUsername(username);
        System.out.println(trainerDTO);
        System.out.println(TrainerConverter.toGetResponseDTO(trainerDTO));
        return new ResponseEntity<>(TrainerConverter.toGetResponseDTO(trainerDTO), HttpStatus.FOUND);
    }

    @PutMapping
    @Operation(summary = "Update trainer", description = "Updates trainer information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated trainer"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<TrainerUpdateResponseDTO> updateTrainer(@Valid @RequestBody TrainerUpdateRequestDTO trainer) throws EntityNotFoundException {
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
        List<TrainingByTrainerDTO> trainings = trainerService.getTrainerTrainingsByCriteria(username, fromDate, toDate, traineeName);
        return new ResponseEntity<>(trainings, HttpStatus.OK);
    }
}
