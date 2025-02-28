package org.example.crmsystem.controller;

import lombok.RequiredArgsConstructor;
import org.example.crmsystem.converter.TraineeConverter;
import org.example.crmsystem.dto.trainee.TraineeServiceDTO;
import org.example.crmsystem.dto.trainee.request.TraineeRegistrationRequestDTO;
import org.example.crmsystem.dto.trainee.request.TraineeUpdateRequestDTO;
import org.example.crmsystem.dto.trainee.response.TraineeGetResponseDTO;
import org.example.crmsystem.dto.trainee.response.TraineeUpdateResponseDTO;
import org.example.crmsystem.dto.trainer.TrainerNestedDTO;
import org.example.crmsystem.dto.training.TrainingByTraineeDTO;
import org.example.crmsystem.dto.user.UserCredentialsDTO;
import org.example.crmsystem.dto.user.UserUpdateStatusRequestDTO;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.service.TraineeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/trainees", produces = {"application/JSON"})
@RequiredArgsConstructor
public class TraineeController {
    private final TraineeService traineeService;

    @PostMapping
    public ResponseEntity<UserCredentialsDTO> registerTrainee(@RequestBody TraineeRegistrationRequestDTO trainee) {
        TraineeServiceDTO traineeDTO = traineeService.createProfile(TraineeConverter.toServiceDTO(trainee));
        return new ResponseEntity<>(TraineeConverter.toRegistrationResponseDTO(traineeDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TraineeGetResponseDTO> getTrainee(@PathVariable("username") String username) throws EntityNotFoundException {
        TraineeServiceDTO traineeDTO = traineeService.getByUsername(username);
        return new ResponseEntity<>(TraineeConverter.toGetResponseDTO(traineeDTO), HttpStatus.FOUND);
    }

    @PutMapping
    public ResponseEntity<TraineeUpdateResponseDTO> updateTrainee(@RequestBody TraineeUpdateRequestDTO trainee) throws EntityNotFoundException {
        TraineeServiceDTO traineeDTO = traineeService.update(TraineeConverter.toServiceDTO(trainee));
        return new ResponseEntity<>(TraineeConverter.toUpdateResponseDTO(traineeDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<HttpStatus> deleteTrainee(@PathVariable("username") String username) {
        traineeService.deleteByUsername(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /// ////////////////////////////////////////
    @PatchMapping
    public ResponseEntity<HttpStatus> toggleActiveStatus(@RequestBody UserUpdateStatusRequestDTO trainee) throws EntityNotFoundException {
        System.out.println(trainee);
        traineeService.toggleActiveStatus(trainee.getUsername(), trainee.isActive());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{username}/trainers/unsigned")
    public ResponseEntity<List<TrainerNestedDTO>> getTrainersNotAssignedToTrainee(@PathVariable("username") String username) {
        List<TrainerNestedDTO> trainers = traineeService.getTrainersNotAssignedToTrainee(username);
        return new ResponseEntity<>(trainers, HttpStatus.OK);
    }

    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainingByTraineeDTO>> getTraineeTrainings(
            @PathVariable("username") String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingType
    ) {
        List<TrainingByTraineeDTO> trainings = traineeService.getTraineeTrainingsByCriteria(username, fromDate, toDate, trainerName, trainingType);
        return new ResponseEntity<>(trainings, HttpStatus.OK);
    }

    @PutMapping("/{username}/trainers")
    public ResponseEntity<List<TrainerNestedDTO>> updateTraineeTrainers(
            @PathVariable("username") String username,
            @RequestBody List<String> trainerUsernames) {

        List<TrainerNestedDTO> updatedTrainers = traineeService.updateTraineeTrainers(username, trainerUsernames);
        return new ResponseEntity<>(updatedTrainers, HttpStatus.OK);
    }
}
