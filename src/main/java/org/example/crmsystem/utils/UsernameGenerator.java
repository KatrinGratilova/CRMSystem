package org.example.crmsystem.utils;

import org.example.crmsystem.dao.interfaces.TraineeRepositoryCustom;
import org.example.crmsystem.dao.interfaces.TrainerRepositoryCustom;
import org.example.crmsystem.dto.user.UserServiceDTO;
import org.springframework.stereotype.Component;

@Component
public class UsernameGenerator {
    TraineeRepositoryCustom traineeDAO;
    TrainerRepositoryCustom trainerDAO;

    public UsernameGenerator(TraineeRepositoryCustom traineeDAO, TrainerRepositoryCustom trainerDAO) {
        this.traineeDAO = traineeDAO;
        this.trainerDAO = trainerDAO;
    }

    public String generateUsername(UserServiceDTO userDTO) {
        String userName = userDTO.getFirstName() + "." + userDTO.getLastName();
        int suffix = traineeDAO.getWhereUsernameStartsWith(userName).size() + trainerDAO.getWhereUsernameStartsWith(userName).size();
        return suffix == 0 ? userName : userName + suffix;
    }
}
