package org.example.crmsystem.controller;

import lombok.RequiredArgsConstructor;
import org.example.crmsystem.converter.TrainingConverter;
import org.example.crmsystem.dto.training.TrainingAddRequestDTO;
import org.example.crmsystem.exception.EntityNotFoundException;
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
public class TrainingController {
    private final TrainingService trainingService;

    @PostMapping
    public ResponseEntity<HttpStatus> addTraining(@RequestBody TrainingAddRequestDTO training) throws UserIsNotAuthenticated, EntityNotFoundException {
        trainingService.add(TrainingConverter.toServiceDTO(training));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
