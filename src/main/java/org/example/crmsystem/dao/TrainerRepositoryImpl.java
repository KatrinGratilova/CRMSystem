package org.example.crmsystem.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.example.crmsystem.dao.interfaces.TrainerRepositoryCustom;
import org.example.crmsystem.dao.queries.TrainerQueries;
import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.messages.LogMessages;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Log4j2
@RequiredArgsConstructor
public class TrainerRepositoryImpl implements TrainerRepositoryCustom {
    private final EntityManager entityManager;

    @Override
    @Transactional
    public Optional<TrainerEntity> getById(long id) {

        return Optional.ofNullable(entityManager.find(TrainerEntity.class, id));

    }

    @Override
    @Transactional
    public Optional<TrainerEntity> getByUsername(String username) {

        Query<TrainerEntity> query = (Query<TrainerEntity>) entityManager.createQuery(TrainerQueries.GET_BY_USERNAME.getQuery(), TrainerEntity.class);
        query.setParameter("username", username);

        List<TrainerEntity> resultList = query.getResultList();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));

    }

    @Override
    @Transactional
    public List<TrainerEntity> getWhereUsernameStartsWith(String username) {
        Query<TrainerEntity> query = (Query<TrainerEntity>) entityManager.createQuery(TrainerQueries.GET_WHERE_USERNAME_STARTS_WITH.getQuery(), TrainerEntity.class);
        query.setParameter("username", username + "%");

        return query.getResultList();
    }


    @Override
    @Transactional
    public TrainerEntity update(TrainerEntity trainerModified) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");
        TrainerEntity trainer = null;

        try {
            Optional<TrainerEntity> trainerOptional = getByUsername(trainerModified.getUsername());
            if (trainerOptional.isPresent()) {
                trainer = trainerOptional.get();

                trainer.setFirstName(trainerModified.getFirstName());
                trainer.setLastName(trainerModified.getLastName());
                trainer.setSpecialization(trainerModified.getSpecialization());
                trainer.setActive(trainerModified.isActive());

                entityManager.merge(trainer);
            } else
                throw new EntityNotFoundException(ExceptionMessages.TRAINER_NOT_FOUND.format(trainerModified.getUsername()));
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
        }
        return trainer;
    }

    @Override
    @Transactional
    public void updatePassword(TrainerEntity trainerEntity) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");

        try {

            Optional<TrainerEntity> traineeOptional = getByUsername(trainerEntity.getUsername());
            if (traineeOptional.isPresent()) {
                TrainerEntity savedTrainee = traineeOptional.get();

                trainerEntity.setId(savedTrainee.getId());
                entityManager.merge(trainerEntity);
            } else
                throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(trainerEntity.getUsername()));

        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean toggleActiveStatus(String username, boolean isActive) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");

        try {
            Optional<TrainerEntity> trainerOptional = getByUsername(username);
            TrainerEntity trainer;
            if (trainerOptional.isPresent()) {
                trainer = trainerOptional.get();
                trainer.setActive(isActive);
                entityManager.merge(trainer);

                return true;
            } else {
                throw new EntityNotFoundException(ExceptionMessages.TRAINER_NOT_FOUND.format(username));
            }
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
            return false;
        }
    }

    public List<TrainingEntity> getTrainerTrainingsByCriteria(
            String trainerUserName, LocalDateTime fromDate, LocalDateTime toDate, String traineeName) {
        String transactionId = ThreadContext.get("transactionId");

        try {

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<TrainingEntity> cq = cb.createQuery(TrainingEntity.class);
            Root<TrainingEntity> root = cq.from(TrainingEntity.class);

            List<Predicate> predicates = new ArrayList<>();

            Join<TrainingEntity, TrainerEntity> trainerJoin = root.join("trainer");
            predicates.add(cb.equal(trainerJoin.get("username"), trainerUserName));

            if (fromDate != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("trainingDate"), fromDate));
            if (toDate != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("trainingDate"), toDate));

            if (traineeName != null && !traineeName.isEmpty()) {
                Join<TrainingEntity, TraineeEntity> traineeJoin = root.join("trainee");
                predicates.add(cb.equal(traineeJoin.get("username"), traineeName));
            }
            cq.where(predicates.toArray(new Predicate[0]));

            return entityManager.createQuery(cq).getResultList();
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
        }
        return new ArrayList<>();
    }
}
