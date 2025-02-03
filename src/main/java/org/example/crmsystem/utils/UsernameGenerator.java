package org.example.crmsystem.utils;

import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.model.User;
import org.springframework.stereotype.Component;

@Component
public class UsernameGenerator {
    TraineeDAO traineeDAO;
    TrainerDAO trainerDAO;

    public UsernameGenerator(TraineeDAO traineeDAO, TrainerDAO trainerDAO) {
        this.traineeDAO = traineeDAO;
        this.trainerDAO = trainerDAO;
    }

    public String generateUserName(User user) {
        String userName = user.getFirstName() + "." + user.getLastName();
        int suffix = traineeDAO.getByUserName(userName).size() + trainerDAO.getByUserName(userName).size();
        return suffix == 0 ? userName : userName + suffix;
    }
}
