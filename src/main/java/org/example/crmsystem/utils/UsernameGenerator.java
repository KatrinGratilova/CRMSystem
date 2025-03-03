package org.example.crmsystem.utils;

import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.dto.user.UserServiceDTO;
import org.springframework.stereotype.Component;

@Component
public class UsernameGenerator {
    TraineeDAO traineeDAO;
    TrainerDAO trainerDAO;

    public UsernameGenerator(TraineeDAO traineeDAO, TrainerDAO trainerDAO) {
        this.traineeDAO = traineeDAO;
        this.trainerDAO = trainerDAO;
    }

    public String generateUsername(UserServiceDTO userDTO) {
        String userName = userDTO.getFirstName() + "." + userDTO.getLastName();
        int suffix = traineeDAO.getWhereUsernameStartsWith(userName).size() + trainerDAO.getWhereUsernameStartsWith(userName).size();
        return suffix == 0 ? userName : userName + suffix;
    }
}
