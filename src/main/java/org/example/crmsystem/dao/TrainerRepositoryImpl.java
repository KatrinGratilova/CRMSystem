package org.example.crmsystem.dao;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.dao.queries.TrainerQueries;
import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.TrainingEntity;
import jakarta.persistence.EntityNotFoundException;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.messages.LogMessages;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Log4j2
@RequiredArgsConstructor
public class TrainerRepositoryImpl implements TrainerDAO {
    private final SessionFactory sessionFactory;

    @Override
    public TrainerEntity add(TrainerEntity trainerEntity) {
        String transactionId = ThreadContext.get("transactionId");
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(trainerEntity);
            transaction.commit();
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
            if (transaction != null) transaction.rollback();
        }
        return trainerEntity;
    }

    @Override
    public Optional<TrainerEntity> getById(long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(TrainerEntity.class, id));
        }
    }

    @Override
    public Optional<TrainerEntity> getByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<TrainerEntity> query = session.createQuery(TrainerQueries.GET_BY_USERNAME.getQuery(), TrainerEntity.class);
            query.setParameter("username", username);

            List<TrainerEntity> resultList = query.getResultList();
            return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
        }
    }

    @Override
    public List<TrainerEntity> getWhereUsernameStartsWith(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<TrainerEntity> query = session.createQuery(TrainerQueries.GET_WHERE_USERNAME_STARTS_WITH.getQuery(), TrainerEntity.class);
            query.setParameter("username", username + "%");

            return query.getResultList();
        }
    }

    @Override
    public TrainerEntity update(TrainerEntity trainerModified) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");
        Transaction transaction = null;
        TrainerEntity trainer = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Optional<TrainerEntity> trainerOptional = getByUsername(trainerModified.getUsername());
            if (trainerOptional.isPresent()) {
                trainer = trainerOptional.get();

                trainer.setFirstName(trainerModified.getFirstName());
                trainer.setLastName(trainerModified.getLastName());
                trainer.setSpecialization(trainerModified.getSpecialization());
                trainer.setActive(trainerModified.isActive());

                session.merge(trainer);
            } else
                throw new EntityNotFoundException(ExceptionMessages.TRAINER_NOT_FOUND.format(trainerModified.getUsername()));
            transaction.commit();
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
            if (transaction != null) transaction.rollback();
        }
        return trainer;
    }

    @Override
    public void updatePassword(TrainerEntity trainerEntity) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Optional<TrainerEntity> traineeOptional = getByUsername(trainerEntity.getUsername());
            if (traineeOptional.isPresent()) {
                TrainerEntity savedTrainee = traineeOptional.get();

                trainerEntity.setId(savedTrainee.getId());
                session.merge(trainerEntity);
            } else
                throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(trainerEntity.getUsername()));
            transaction.commit();
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public boolean toggleActiveStatus(String username, boolean isActive) throws EntityNotFoundException {
        String transactionId = ThreadContext.get("transactionId");
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Optional<TrainerEntity> trainerOptional = getByUsername(username);
            TrainerEntity trainer;
            if (trainerOptional.isPresent()) {
                trainer = trainerOptional.get();
                trainer.setActive(isActive);
                session.merge(trainer);

                transaction.commit();
                return true;
            } else {
                throw new EntityNotFoundException(ExceptionMessages.TRAINER_NOT_FOUND.format(username));
            }
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
            if (transaction != null) transaction.rollback();
            return false;
        }
    }

    public List<TrainingEntity> getTrainerTrainingsByCriteria(
            String trainerUserName, LocalDateTime fromDate, LocalDateTime toDate, String traineeName) {
        String transactionId = ThreadContext.get("transactionId");
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
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
            transaction.commit();

            return session.createQuery(cq).getResultList();
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
            if (transaction != null) transaction.rollback();
        }
        return new ArrayList<>();
    }
}
