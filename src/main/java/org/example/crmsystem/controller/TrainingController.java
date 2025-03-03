package org.example.crmsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.crmsystem.converter.TrainingConverter;
import org.example.crmsystem.dto.training.TrainingAddRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import org.example.crmsystem.exception.UserIsNotAuthenticated;
import org.example.crmsystem.service.TrainingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/trainings", produces = {"application/JSON"})
@RequiredArgsConstructor
@Tag(name = "Training Controller", description = "Operations related to trainings")
public class TrainingController {
    private final TrainingService trainingService;

    @PostMapping
    @Operation(summary = "Register a new training", description = "Creates a new training in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered a new training"),
            @ApiResponse(responseCode = "404", description = "Trainer or trainee not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<HttpStatus> addTraining(@Valid @RequestBody TrainingAddRequestDTO training) throws UserIsNotAuthenticated, EntityNotFoundException {
        trainingService.add(TrainingConverter.toServiceDTO(training));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
