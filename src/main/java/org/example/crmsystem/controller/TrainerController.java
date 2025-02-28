package org.example.crmsystem.controller;

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
import org.example.crmsystem.exception.EntityNotFoundException;
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
public class TrainerController {
    private final TrainerService trainerService;

    @PostMapping
    public ResponseEntity<UserCredentialsDTO> registerTrainer(@RequestBody TrainerRegistrationRequestDTO trainer) {
        TrainerServiceDTO trainerDTO = trainerService.createProfile(TrainerConverter.toServiceDTO(trainer));
        return new ResponseEntity<>(TrainerConverter.toRegistrationResponseDTO(trainerDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerGetResponseDTO> getTrainer(@PathVariable String username) throws EntityNotFoundException {
        TrainerServiceDTO trainerDTO = trainerService.getByUsername(username);
        System.out.println(trainerDTO);
        System.out.println(TrainerConverter.toGetResponseDTO(trainerDTO));
        return new ResponseEntity<>(TrainerConverter.toGetResponseDTO(trainerDTO), HttpStatus.FOUND);
    }

    @PutMapping
    public ResponseEntity<TrainerUpdateResponseDTO> updateTrainer(@RequestBody TrainerUpdateRequestDTO trainer) throws EntityNotFoundException {
        TrainerServiceDTO trainerDTO = trainerService.update(TrainerConverter.toServiceDTO(trainer));
        return new ResponseEntity<>(TrainerConverter.toUpdateResponseDTO(trainerDTO), HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<HttpStatus> toggleActiveStatus(@RequestBody UserUpdateStatusRequestDTO trainer) throws EntityNotFoundException {
        trainerService.toggleActiveStatus(trainer.getUsername(), trainer.isActive());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{username}/trainings")
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
