package org.example.crmsystem.utils;

import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsernameGenerator {
    @Autowired
    TraineeDAO traineeRepository;

    public String generateUserName(User user) {
        String userName = user.getFirstName() + "." + user.getLastName();
        int suffix = traineeRepository.getByUserName(userName).size();
        return suffix == 0 ? userName : userName + suffix;
    }
}
