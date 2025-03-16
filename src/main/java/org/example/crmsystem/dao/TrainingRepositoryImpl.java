package org.example.crmsystem.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.example.crmsystem.dao.interfaces.TrainingDAO;
import org.example.crmsystem.dao.queries.TrainingQueries;
import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.exception.IncompatibleSpecialization;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.messages.LogMessages;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Log4j2
@RequiredArgsConstructor
public class TrainingRepositoryImpl implements TrainingDAO {
    private final EntityManager entityManager;

    @Override
    @Transactional
    public TrainingEntity create(TrainingEntity trainingEntity) throws IncompatibleSpecialization {
        String transactionId = ThreadContext.get("transactionId");

        try {
            TraineeEntity trainee = entityManager.find(TraineeEntity.class, trainingEntity.getTrainee().getId());
            TrainerEntity trainer = entityManager.find(TrainerEntity.class, trainingEntity.getTrainer().getId());

            if (!trainer.getSpecialization().equals(trainingEntity.getTrainingType())) {
                log.warn(LogMessages.INCOMPATIBLE_SPECIALIZATION.getMessage(), transactionId, trainer.getUsername(), trainingEntity.getTrainingName());
                throw new IncompatibleSpecialization(ExceptionMessages.INCOMPATIBLE_SPECIALIZATION.format(trainer.getUsername(), trainingEntity.getTrainingName()));
            }

            trainee.getTrainers().add(trainingEntity.getTrainer());
            trainer.getTrainees().add(trainingEntity.getTrainee());

            trainingEntity.setTrainee(entityManager.merge(trainee));
            trainingEntity.setTrainer(entityManager.merge(trainer));

            entityManager.persist(trainingEntity);
        } catch (IncompatibleSpecialization e) {
            throw e;
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
        return trainingEntity;
    }

    @Override
    public Optional<TrainingEntity> getById(long id) {
        return Optional.ofNullable(entityManager.find(TrainingEntity.class, id));
    }

    @Override
    public List<TrainingEntity> getByName(String name) {
        TypedQuery<TrainingEntity> jpqlQuery = entityManager.createQuery(TrainingQueries.GET_BY_NAME.getQuery(), TrainingEntity.class);
        jpqlQuery.setParameter("name", name + "%");

        return jpqlQuery.getResultList();
    }
}
