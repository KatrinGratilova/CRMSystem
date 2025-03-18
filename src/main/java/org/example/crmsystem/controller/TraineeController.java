package org.example.crmsystem.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.crmsystem.security.JwtTokenUtil;
import org.example.crmsystem.converter.TraineeConverter;
import org.example.crmsystem.dto.trainee.TraineeServiceDTO;
import org.example.crmsystem.dto.trainee.request.TraineeRegistrationRequestDTO;
import org.example.crmsystem.dto.trainee.request.TraineeUpdateRequestDTO;
import org.example.crmsystem.dto.trainee.response.TraineeGetResponseDTO;
import org.example.crmsystem.dto.trainee.response.TraineeUpdateResponseDTO;
import org.example.crmsystem.dto.trainer.TrainerNestedDTO;
import org.example.crmsystem.dto.training.TrainingByTraineeDTO;
import org.example.crmsystem.dto.user.UserRegisterResponseDTO;
import org.example.crmsystem.dto.user.UserUpdateStatusRequestDTO;
import org.example.crmsystem.service.TraineeService;
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
@RequestMapping(value = "/trainees", produces = {"application/JSON"})
@RequiredArgsConstructor
@Tag(name = "Trainee Controller", description = "Operations related to trainees")
public class TraineeController {
    private final TraineeService traineeService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping
    @Operation(summary = "Register a new trainee", description = "Creates a new trainee in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered a new trainee"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<UserRegisterResponseDTO> registerTrainee(@Valid @RequestBody TraineeRegistrationRequestDTO trainee) {
        TraineeServiceDTO traineeDTO = traineeService.createProfile(TraineeConverter.toServiceDTO(trainee));
        UserRegisterResponseDTO responseDTO = TraineeConverter.toUserRegistrationResponseDTO(traineeDTO);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(responseDTO.getUsername(), responseDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String jwt = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());
        responseDTO.setToken(jwt);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get trainee by username", description = "Retrieves trainee details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainee"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<TraineeGetResponseDTO> getTrainee(@PathVariable("username") String username) throws EntityNotFoundException {
        TraineeServiceDTO traineeDTO = traineeService.getByUsername(username);
        return new ResponseEntity<>(TraineeConverter.toGetResponseDTO(traineeDTO), HttpStatus.FOUND);
    }

    @PutMapping
    @Operation(summary = "Update trainee", description = "Updates trainee information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated trainee"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<TraineeUpdateResponseDTO> updateTrainee(@Valid @RequestBody TraineeUpdateRequestDTO trainee) throws EntityNotFoundException {
        TraineeServiceDTO traineeDTO = traineeService.update(TraineeConverter.toServiceDTO(trainee));
        return new ResponseEntity<>(TraineeConverter.toUpdateResponseDTO(traineeDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Delete trainee by username", description = "Deletes trainee details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainee"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<HttpStatus> deleteTrainee(@PathVariable("username") String username) {
        traineeService.deleteByUsername(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{username}/trainers/unsigned")
    @Operation(summary = "Get trainers not assigned to trainee by username", description = "Retrieves trainee trainers that are not assigned")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved unassigned trainers"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<List<TrainerNestedDTO>> getTrainersNotAssignedToTrainee(@PathVariable("username") String username) {
        List<TrainerNestedDTO> trainers = traineeService.getTrainersNotAssignedToTrainee(username);
        return new ResponseEntity<>(trainers, HttpStatus.OK);
    }

    @PutMapping("/{username}/trainers")
    @Operation(summary = "Update trainee trainers list by username", description = "Updated trainee trainers list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated trainee trainers list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<List<TrainerNestedDTO>> updateTraineeTrainers(
            @PathVariable("username") String username,
            @NotNull @RequestBody List<String> trainerUsernames) throws EntityNotFoundException {
        List<TrainerNestedDTO> updatedTrainers = traineeService.updateTraineeTrainers(username, trainerUsernames);
        return new ResponseEntity<>(updatedTrainers, HttpStatus.OK);
    }

    @GetMapping("/{username}/trainings")
    @Operation(summary = "Get trainee trainings by username and criteria", description = "Retrieves trainee trainings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainee trainings by criteria"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<List<TrainingByTraineeDTO>> getTraineeTrainings(
            @PathVariable("username") String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(required = false) String trainerUsername,
            @RequestParam(required = false) String trainingType
    ) {
        List<TrainingByTraineeDTO> trainings = traineeService.getTraineeTrainingsByCriteria(username, fromDate, toDate, trainerUsername, trainingType);
        return new ResponseEntity<>(trainings, HttpStatus.OK);
    }

    @PatchMapping("/{username}/status")
    @Operation(summary = "Update trainee active status by username", description = "Updates trainee status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated trainee status"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<HttpStatus> toggleActiveStatus(@PathVariable String username, @Valid @RequestBody UserUpdateStatusRequestDTO request) throws EntityNotFoundException {
        traineeService.toggleActiveStatus(username, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
