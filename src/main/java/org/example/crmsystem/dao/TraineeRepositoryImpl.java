package org.example.crmsystem.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.example.crmsystem.dao.interfaces.TraineeRepositoryCustom;
import org.example.crmsystem.dao.queries.TraineeQueries;
import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.entity.TrainingTypeEntity;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.messages.LogMessages;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Repository
@Log4j2
@RequiredArgsConstructor
public class TraineeRepositoryImpl implements TraineeRepositoryCustom {
    private final EntityManager entityManager;

    @Override
    @Transactional
    public Optional<TraineeEntity> getById(long id) {
        return Optional.ofNullable(entityManager.find(TraineeEntity.class, id));
    }

    @Override
    @Transactional
    public Optional<TraineeEntity> getByUsername(String username) {
        TypedQuery<TraineeEntity> query = entityManager.createQuery(TraineeQueries.GET_BY_USERNAME.getQuery(), TraineeEntity.class);
        query.setParameter("username", username);

        List<TraineeEntity> resultList = query.getResultList();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
    }

    @Override
    @Transactional
    public List<TraineeEntity> getWhereUsernameStartsWith(String username) {
        TypedQuery<TraineeEntity> query = entityManager.createQuery(TraineeQueries.GET_WHERE_USERNAME_STARTS_WITH.getQuery(), TraineeEntity.class);
        query.setParameter("username", username + "%");

        return query.getResultList();
    }

    @Override
    @Transactional
    public TraineeEntity update(TraineeEntity traineeModified) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");
        TraineeEntity trainee = null;

        try {
            Optional<TraineeEntity> traineeOptional = getByUsername(traineeModified.getUsername());
            if (traineeOptional.isPresent()) {
                trainee = traineeOptional.get();

                trainee.setFirstName(traineeModified.getFirstName());
                trainee.setLastName(traineeModified.getLastName());
                trainee.setDateOfBirth(traineeModified.getDateOfBirth());
                trainee.setAddress(traineeModified.getAddress());
                trainee.setActive(traineeModified.isActive());

                entityManager.merge(trainee);
            } else
                throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(traineeModified.getUsername()));

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
        }
        return trainee;
    }

    @Override
    @Transactional
    public TraineeEntity updateTrainers(TraineeEntity traineeModified) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");
        TraineeEntity trainee = null;

        try {
            Optional<TraineeEntity> traineeOptional = getByUsername(traineeModified.getUsername());
            if (traineeOptional.isPresent()) {
                trainee = traineeOptional.get();

                Set<TrainerEntity> oldTrainers = new HashSet<>(trainee.getTrainers());
                oldTrainers.addAll(traineeModified.getTrainers());

                List<TrainerEntity> updatedTrainers = new ArrayList<>(oldTrainers);

                trainee.setTrainers(updatedTrainers);
                entityManager.merge(trainee);
            } else
                throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(traineeModified.getUsername()));
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
        }
        return trainee;
    }

    @Override
    @Transactional
    public void updatePassword(TraineeEntity traineeEntity) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");

        try {
            Optional<TraineeEntity> traineeOptional = getByUsername(traineeEntity.getUsername());
            if (traineeOptional.isPresent()) {
                TraineeEntity savedTrainee = traineeOptional.get();

                traineeEntity.setId(savedTrainee.getId());
                entityManager.merge(traineeEntity);
            } else
                throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(traineeEntity.getUsername()));

        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteByUsername(String username) {
        String transactionId = ThreadContext.get("transactionId");
        try {
            Optional<TraineeEntity> trainee = getByUsername(username);
            trainee.ifPresent(entity -> {
                // Удаляем все тренировки, если они есть
                entity.getTrainings().forEach(entityManager::remove);
                // Удаляем самого trainee
                entityManager.remove(entity);
            });
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void delete(TraineeEntity traineeEntity) {
        String transactionId = ThreadContext.get("transactionId");

        try {
            Optional<TraineeEntity> trainee = getById(traineeEntity.getId());
            trainee.ifPresent(entityManager::remove);
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean toggleActiveStatus(String username, boolean isActive) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");

        try {
            Optional<TraineeEntity> traineeOptional = getByUsername(username);
            TraineeEntity trainee;
            if (traineeOptional.isPresent()) {
                trainee = traineeOptional.get();
                trainee.setActive(isActive);
                entityManager.merge(trainee);

                return true;
            } else {
                throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(username));
            }
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public List<TrainingEntity> getTraineeTrainingsByCriteria(
            String traineeUserName, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType) {
        String transactionId = ThreadContext.get("transactionId");

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<TrainingEntity> cq = cb.createQuery(TrainingEntity.class);
            Root<TrainingEntity> root = cq.from(TrainingEntity.class);

            List<Predicate> predicates = new ArrayList<>();

            Join<TrainingEntity, TraineeEntity> traineeJoin = root.join("trainee");
            predicates.add(cb.equal(traineeJoin.get("username"), traineeUserName));

            if (fromDate != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("trainingDate"), fromDate));
            if (toDate != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("trainingDate"), toDate));

            if (trainerName != null && !trainerName.isEmpty()) {
                Join<TrainingEntity, TrainerEntity> trainerJoin = root.join("trainer");
                predicates.add(cb.equal(trainerJoin.get("username"), trainerName));
            }
            if (trainingType != null && !trainingType.isEmpty()) {
                Join<TrainingEntity, TrainingTypeEntity> trainingTypeJoin = root.join("trainingType");
                predicates.add(cb.equal(trainingTypeJoin.get("trainingType"), trainingType));
            }
            cq.where(predicates.toArray(new Predicate[0]));

            return entityManager.createQuery(cq).getResultList();
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public List<TrainerEntity> getTrainersNotAssignedToTrainee(String traineeUserName) {
        String transactionId = ThreadContext.get("transactionId");
        List<TrainerEntity> result = new ArrayList<>();

        try {
            TypedQuery<TrainerEntity> query = entityManager.createQuery(TraineeQueries.GET_TRAINERS_NOT_ASSIGNED_TO_TRAINEE.getQuery(), TrainerEntity.class);
            query.setParameter("traineeUserName", traineeUserName);

            result = query.getResultList();
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
        }
        return result;
    }
}
