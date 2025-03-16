package org.example.crmsystem.dao.interfaces;

import org.example.crmsystem.entity.TraineeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TraineeDAO extends JpaRepository<TraineeEntity, Long> {
    @Query("SELECT t FROM TraineeEntity t WHERE t.isActive = :active")
    List<TraineeEntity> findByActive(boolean active);
}
