package org.example.crmsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.crmsystem.entity.TrainingTypeEntity;
import org.example.crmsystem.service.TrainingTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/training-types", produces = {"application/JSON"})
@RequiredArgsConstructor
@Tag(name = "Training Type Controller", description = "Operations related to training types")
public class TrainingTypeController {
    private final TrainingTypeService trainingTypeService;

    @GetMapping
    @Operation(summary = "Retrieve all training types", description = "Retrieves all training types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully retrieved all training types"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    })
    public ResponseEntity<List<TrainingTypeEntity>> getAllTrainingTypes() {
        return new ResponseEntity<>(trainingTypeService.getAll(), HttpStatus.FOUND);
    }
}
