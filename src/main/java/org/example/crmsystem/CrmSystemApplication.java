package org.example.crmsystem;

import org.example.crmsystem.facade.GymFacade;
import org.example.crmsystem.model.Trainee;
import org.example.crmsystem.model.Trainer;
import org.example.crmsystem.model.Training;
import org.example.crmsystem.model.TrainingType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ComponentScan(basePackages = "org.example.crmsystem")
public class CrmSystemApplication {

    public static void main(String[] args) {
        try (ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(CrmSystemApplication.class)) {
            GymFacade gymFacade = ctx.getBean(GymFacade.class);

            Trainee trainee = gymFacade.createTraineeProfile(Trainee.builder()
                    .dateOfBirth(LocalDate.of(2000, 12, 12))
                    .address("Yk")
                    .firstName("Andrew")
                    .lastName("Bob")
                    .isActive(true)
                    .build());

            Trainer trainer = gymFacade.createTrainerProfile(Trainer.builder()
                    .firstName("Bob")
                    .lastName("Ross")
                    .specialization(TrainingType.YOGA)
                    .isActive(true)
                    .build());

            Training training = gymFacade.createTrainingProfile(Training.builder()
                    .trainingName("First Yoga Training")
                    .trainingDate(LocalDateTime.of(2024, 12, 12, 12, 0, 0))
                    .trainingType(TrainingType.YOGA)
                    .trainingTime(120)
                    .build());

            gymFacade.planTraining(training.getTrainingId(), trainee.getTraineeId(), trainer.getTrainerId());
        }
    }
}
