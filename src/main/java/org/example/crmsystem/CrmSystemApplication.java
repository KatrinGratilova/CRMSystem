package org.example.crmsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@ComponentScan(basePackages = "org.example.crmsystem")
@SpringBootApplication
public class CrmSystemApplication {

    public static void main(String[] args)  {
        SpringApplication.run(CrmSystemApplication.class, args);
//        try (ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(CrmSystemApplication.class)) {
//            GymFacade gymFacade = ctx.getBean(GymFacade.class);
//            TraineeService traineeService = ctx.getBean(TraineeService.class);
//            TrainerService trainerService = ctx.getBean(TrainerService.class);
//            TraineeDAO traineeRepository = ctx.getBean(TraineeRepositoryImpl.class);
//            TrainingService trainingService = ctx.getBean(TrainingService.class);
//            TrainingTypeService trainingTypeService = ctx.getBean(TrainingTypeService.class);
           // trainingTypeService.create(new TrainingTypeEntity());

//            TraineeEntity traineeEntity = gymFacade.createTraineeProfile(TraineeEntity.builder()
//                    .dateOfBirth(LocalDate.of(2000, 12, 12))
//                    .address("Yk")
//                    .firstName("Andrew")
//                    .lastName("Bob")
//                    .isActive(true)
//                    .trainings(new ArrayList<>())
//                    .trainers(new ArrayList<>())
//                    .build());

//            System.out.println(traineeEntity);
//
//            TrainerEntity trainerEntity = gymFacade.createTrainerProfile(TrainerEntity.builder()
//                    .firstName("Bob")
//                    .lastName("Ross")
//                    .specialization(trainingTypeService.getById(1))
//                    .isActive(true)
//                    .trainings(new ArrayList<>())
//                    .trainees(new ArrayList<>())
//                    .build());
//
//            System.out.println(trainerEntity);
//
//
//            TrainerEntity trainerEntity2 = gymFacade.createTrainerProfile(TrainerEntity.builder()
//                    .firstName("Bobcffddddd")
//                    .lastName("Ross")
//                    .specialization(trainingTypeService.getById(1))
//                    .isActive(true)
//                    .trainings(new ArrayList<>())
//                    .trainees(new ArrayList<>())
//                    .build());

//            TrainingEntity trainingEntity = gymFacade.createTrainingProfile(TrainingEntity.builder()
//                    .trainingName("First Yoga TrainingEntity")
//                    .trainingDate(LocalDateTime.of(2024, 12, 12, 12, 0, 0))
//                    .trainingType(trainingTypeService.getById(1))
//                    .trainingDuration(120)
//                    .trainee(traineeEntity)
//                    .trainer(trainerEntity)
//                    .build());
//
//            TrainingEntity trainingEntity2 = gymFacade.createTrainingProfile(TrainingEntity.builder()
//                    .trainingName("First Yoga TrainingEntity")
//                    .trainingDate(LocalDateTime.of(2024, 12, 12, 12, 0, 0))
//                    .trainingType(trainingTypeService.getById(1))
//                    .trainingDuration(120)
//                    .trainee(traineeEntity)
//                    .trainer(trainerEntity2)
//                    .build());
//
//
//            TrainingEntity trainingEntity3 = gymFacade.createTrainingProfile(TrainingEntity.builder()
//                    .trainingName("First Yoga TrainingEntity")
//                    .trainingDate(LocalDateTime.of(2022, 12, 12, 12, 0, 0))
//                    .trainingType(trainingTypeService.getById(2))
//                    .trainingDuration(120)
//                    .trainee(traineeEntity)
//                    .trainer(trainerEntity2)
//                    .build());
//
//            TrainingEntity trainingEntity4 = gymFacade.createTrainingProfile(TrainingEntity.builder()
//                    .trainingName("First Yoga TrainingEntity")
//                    .trainingDate(LocalDateTime.of(2023, 12, 12, 12, 0, 0))
//                    .trainingType(trainingTypeService.getById(2))
//                    .trainingDuration(120)
//                    .trainee(traineeEntity)
//                    .trainer(trainerEntity2)
//                    .build());
//
//            System.out.println(trainingEntity);
//            System.out.println(traineeService.getById(traineeEntity.getId()));
//            System.out.println(trainerService.getById(trainerEntity.getId()));

//           System.out.println(trainerService.getTrainersNotAssignedToTrainee("Andrew.Bob"));

//           traineeService.toggleActiveStatus(//            System.out.println();
//            for (TrainingEntity training : list) {
//                System.out.println(training);
//            }
//            var list1 = traineeService.getTraineeTrainingsByCriteria("Andrew.Bob", null ,null, trainerEntity2.getUserName(), null);
//
//            System.out.println();
//            for (TrainingEntity training : list1) {
//                System.out.println(training);
//            }
//            var list2 = traineeService.getTraineeTrainingsByCriteria("Andrew.Bob", null ,null, trainerEntity2.getUserName(), "FITNESS");
//
//            System.out.println();
//            for (TrainingEntity training : list2) {
//                System.out.println(training);
//            }
//            var list3 = traineeService.getTraineeTrainingsByCriteria("Andrew.Bob", LocalDateTime.of(2021, 12, 12, 12, 0, 0) ,null, trainerEntity2.getUserName(), "FITNESS");
//
//            System.out.println();
//            for (TrainingEntity training : list3) {
//                System.out.println(training);
//            }
//
//            var list4 = traineeService.getTraineeTrainingsByCriteria("Andrew.Bob", LocalDateTime.of(2023, 6, 12, 12, 0, 0) ,LocalDateTime.of(2024, 12, 12, 12, 0, 0) , trainerEntity2.getUserName(), "FITNESS");
//
//            System.out.println();
//            for (TrainingEntity training : list4) {
//                System.out.println(training);
//            }
////
//            TrainingEntity trainingEntity5 = gymFacade.createTrainingProfile(TrainingEntity.builder()
//                    .trainingName("First Yoga TrainingEntity")
//                    .trainingDate(LocalDateTime.of(2023, 12, 12, 12, 0, 0))
//                    .trainingType(trainingTypeService.getById(1))
//                    .trainingDuration(120)
//                    .trainee(null)
//                    .trainer(null)
//                    .build());
//            System.out.println(trainingEntity5);
//            trainingEntity5.setTrainee(traineeEntity);
//            trainingEntity5.setTrainer(trainerEntity);
//            System.out.println(trainingService.update(trainingEntity5));

//            TraineeEntity traineeEntity1 = gymFacade.createTraineeProfile(TraineeEntity.builder()
//                    .dateOfBirth(LocalDate.of(2000, 12, 12))
//                    .address("Yk")
//                    .firstName("Andrew")
//                    .lastName("Bob")
//                    .isActive(true)
//                    .build());
//
//            System.out.println(traineeEntity1);
//
//            traineeService.changePassword("Andrew.Bob1", traineeEntity1.getPassword(), "hello");
//            System.out.println(traineeService.getById(2L));
//

//
//


            //System.out.println(traineeRepository.getByUserName(traineeEntity.getUserName()).get());


            // System.out.println(traineeService.update(traineeEntity));

//            gymFacade.planTraining(trainingEntity.getTrainingId(), traineeEntity.getTraineeId(), trainerEntity.getTrainerId());
        }
    }

