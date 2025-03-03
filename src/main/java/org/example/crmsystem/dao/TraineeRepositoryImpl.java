package org.example.crmsystem.dao;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.dao.queries.TraineeQueries;
import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.entity.TrainingTypeEntity;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.messages.LogMessages;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
@Log4j2
@RequiredArgsConstructor
public class TraineeRepositoryImpl implements TraineeDAO {
    private final SessionFactory sessionFactory;

    @Override
    public TraineeEntity add(TraineeEntity traineeEntity) {
        String transactionId = ThreadContext.get("transactionId");
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(traineeEntity);
            transaction.commit();
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
            if (transaction != null) transaction.rollback();
        }
        return traineeEntity;
    }

    @Override
    public Optional<TraineeEntity> getById(long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(TraineeEntity.class, id));
        }
    }

    @Override
    public Optional<TraineeEntity> getByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<TraineeEntity> query = session.createQuery(TraineeQueries.GET_BY_USERNAME.getQuery(), TraineeEntity.class);
            query.setParameter("username", username);

            List<TraineeEntity> resultList = query.getResultList();
            return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
        }
    }

    @Override
    public List<TraineeEntity> getWhereUsernameStartsWith(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<TraineeEntity> query = session.createQuery(TraineeQueries.GET_WHERE_USERNAME_STARTS_WITH.getQuery(), TraineeEntity.class);
            query.setParameter("username", username + "%");

            return query.getResultList();
        }
    }

    @Override
    public TraineeEntity update(TraineeEntity traineeModified) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");
        Transaction transaction = null;
        TraineeEntity trainee = null;
        Session session = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Optional<TraineeEntity> traineeOptional = getByUsername(traineeModified.getUsername());
            if (traineeOptional.isPresent()) {
                trainee = traineeOptional.get();

                trainee.setFirstName(traineeModified.getFirstName());
                trainee.setLastName(traineeModified.getLastName());
                trainee.setDateOfBirth(traineeModified.getDateOfBirth());
                trainee.setAddress(traineeModified.getAddress());
                trainee.setActive(traineeModified.isActive());

                session.merge(trainee);
            } else
                throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(traineeModified.getUsername()));
            transaction.commit();
        } catch (EntityNotFoundException e) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            throw e;
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
            if (transaction != null && transaction.isActive()) transaction.rollback();
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
        return trainee;
    }

    @Override
    public TraineeEntity updateTrainers(TraineeEntity traineeModified) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");
        Transaction transaction = null;
        TraineeEntity trainee = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Optional<TraineeEntity> traineeOptional = getByUsername(traineeModified.getUsername());
            if (traineeOptional.isPresent()) {
                trainee = traineeOptional.get();

                Set<TrainerEntity> oldTrainers = new HashSet<>(trainee.getTrainers());
                oldTrainers.addAll(traineeModified.getTrainers());

                List<TrainerEntity> updatedTrainers = new ArrayList<>(oldTrainers);

                trainee.setTrainers(updatedTrainers);
                session.merge(trainee);
            } else
                throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(traineeModified.getUsername()));
            transaction.commit();
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
            if (transaction != null) transaction.rollback();
        }
        return trainee;
    }

    @Override
    public void updatePassword(TraineeEntity traineeEntity) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Optional<TraineeEntity> traineeOptional = getByUsername(traineeEntity.getUsername());
            if (traineeOptional.isPresent()) {
                TraineeEntity savedTrainee = traineeOptional.get();

                traineeEntity.setId(savedTrainee.getId());
                session.merge(traineeEntity);
            } else
                throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(traineeEntity.getUsername()));
            transaction.commit();
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public void deleteByUsername(String username) {
        String transactionId = ThreadContext.get("transactionId");
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Optional<TraineeEntity> trainee = getByUsername(username);
            trainee.ifPresent(session::remove);
            transaction.commit();
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public boolean delete(TraineeEntity traineeEntity) {
        String transactionId = ThreadContext.get("transactionId");
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Optional<TraineeEntity> trainee = getById(traineeEntity.getId());
            if (trainee.isPresent()) {
                session.remove(trainee.get());
                transaction.commit();
                return true;
            }
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
            if (transaction != null) transaction.rollback();
        }
        return false;
    }

    @Override
    public boolean toggleActiveStatus(String username, boolean isActive) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Optional<TraineeEntity> traineeOptional = getByUsername(username);
            TraineeEntity trainee;
            if (traineeOptional.isPresent()) {
                trainee = traineeOptional.get();
                trainee.setActive(isActive);
                session.merge(trainee);

                transaction.commit();
                return true;
            } else {
                throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(username));
            }
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
            if (transaction != null) transaction.rollback();
            return false;
        }
    }

    @Override
    public List<TrainingEntity> getTraineeTrainingsByCriteria(
            String traineeUserName, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType) {
        String transactionId = ThreadContext.get("transactionId");
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
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
            transaction.commit();

            return session.createQuery(cq).getResultList();
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
            if (transaction != null) transaction.rollback();
        }
        return new ArrayList<>();
    }

    @Override
    public List<TrainerEntity> getTrainersNotAssignedToTrainee(String traineeUserName) {
        String transactionId = ThreadContext.get("transactionId");
        Transaction transaction = null;
        List<TrainerEntity> result = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            TypedQuery<TrainerEntity> query = session.createQuery(TraineeQueries.GET_TRAINERS_NOT_ASSIGNED_TO_TRAINEE.getQuery(), TrainerEntity.class);
            query.setParameter("traineeUserName", traineeUserName);

            result = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
        }
        return result;
    }
}
