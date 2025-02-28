package org.example.crmsystem.controller;

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
public class TrainingTypeController {
    private final TrainingTypeService trainingTypeService;

    @GetMapping
    public ResponseEntity<List<TrainingTypeEntity>> getAllTrainingTypes() {
        return new ResponseEntity<>(trainingTypeService.getAll(), HttpStatus.FOUND);
    }
}
