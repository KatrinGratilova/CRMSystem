package org.example.crmsystem.dao.interfaces;

import org.example.crmsystem.entity.TrainerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrainerDAO extends JpaRepository<TrainerEntity, Long> {
    @Query("SELECT t FROM TrainerEntity t WHERE t.isActive = :active")
    List<TrainerEntity> findByActive(boolean active);
}
