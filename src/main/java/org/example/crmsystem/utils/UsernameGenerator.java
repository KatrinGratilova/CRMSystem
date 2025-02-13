package org.example.crmsystem.utils;

import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UsernameGenerator {
    TraineeDAO traineeDAO;
    TrainerDAO trainerDAO;

    public UsernameGenerator(TraineeDAO traineeDAO, TrainerDAO trainerDAO) {
        this.traineeDAO = traineeDAO;
        this.trainerDAO = trainerDAO;
    }

    public String generateUserName(UserEntity userEntity) {
        String userName = userEntity.getFirstName() + "." + userEntity.getLastName();
        int suffix = traineeDAO.getWhereUserNameStartsWith(userName).size() + trainerDAO.getWhereUserNameStartsWith(userName).size();
        return suffix == 0 ? userName : userName + suffix;
    }
}
