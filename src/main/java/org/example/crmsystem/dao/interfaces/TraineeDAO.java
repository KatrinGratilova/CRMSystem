package org.example.crmsystem.dao.interfaces;

import org.example.crmsystem.entity.TraineeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface TraineeDAO extends JpaRepository<TraineeEntity, Long>{
}
