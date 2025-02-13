package org.example.crmsystem.dao;

import jakarta.persistence.criteria.*;
import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.dao.interfaces.TraineeDAO;
import org.example.crmsystem.dao.queries.TraineeQueries;
import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.entity.TrainingTypeEntity;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.messages.ExceptionMessages;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Log4j2
public class TraineeRepositoryImpl implements TraineeDAO {
    private final SessionFactory sessionFactory;

    @Autowired
    public TraineeRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public TraineeEntity add(TraineeEntity traineeEntity) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(traineeEntity);
            transaction.commit();
        } catch (Exception e) {
            log.error(e.getMessage());
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
    public Optional<TraineeEntity> getByUserName(String userName) {
        try (Session session = sessionFactory.openSession()) {
            Query<TraineeEntity> query = session.createQuery(TraineeQueries.GET_BY_USERNAME.getQuery(), TraineeEntity.class);
            query.setParameter("userName", userName);

            List<TraineeEntity> resultList = query.getResultList();
            return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
        }
    }

    @Override
    public List<TraineeEntity> getWhereUserNameStartsWith(String userName) {
        try (Session session = sessionFactory.openSession()) {
            Query<TraineeEntity> query = session.createQuery(TraineeQueries.GET_WHERE_USERNAME_STARTS_WITH.getQuery(), TraineeEntity.class);
            query.setParameter("userName", userName + "%");

            return query.getResultList();
        }
    }

    @Override
    public TraineeEntity update(TraineeEntity traineeEntity) throws EntityNotFoundException {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            long id = traineeEntity.getId();

            if (getById(id).isPresent() && id != 0)
                session.merge(traineeEntity);
            else
                throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(id));
            transaction.commit();
        } catch (Exception e) {
            log.error(e.getMessage());
            if (transaction != null) transaction.rollback();
        }
        return traineeEntity;
    }

    @Override
    public void deleteByUserName(String userName) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Optional<TraineeEntity> trainee = getByUserName(userName);
            // trainee.get().getTrainings().clear();
            // session.flush();
            trainee.ifPresent(session::remove);

            transaction.commit();
        } catch (Exception e) {
            log.error(e.getMessage());
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public boolean delete(TraineeEntity traineeEntity) {
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
            log.error(e.getMessage());
            if (transaction != null) transaction.rollback();
        }
        return false;
    }

    @Override
    public boolean toggleActiveStatus(TraineeEntity traineeEntity) throws EntityNotFoundException{
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            TraineeEntity trainee = session.find(TraineeEntity.class, traineeEntity.getId());
            if (trainee != null) {
                trainee.setActive(!trainee.isActive());
                session.merge(trainee);

                transaction.commit();
                return true;
            } else {
                throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(traineeEntity.getId()));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            if (transaction != null) transaction.rollback();
            return false;
        }
    }

    @Override
    public List<TrainingEntity> getTraineeTrainingsByCriteria(
            String traineeUserName, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<TrainingEntity> cq = cb.createQuery(TrainingEntity.class);
            Root<TrainingEntity> root = cq.from(TrainingEntity.class);

            List<Predicate> predicates = new ArrayList<>();

            Join<TrainingEntity, TraineeEntity> traineeJoin = root.join("trainee");
            predicates.add(cb.equal(traineeJoin.get("userName"), traineeUserName));

            if (fromDate != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("trainingDate"), fromDate));
            if (toDate != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("trainingDate"), toDate));

            if (trainerName != null && !trainerName.isEmpty()) {
                Join<TrainingEntity, TrainerEntity> trainerJoin = root.join("trainer");
                predicates.add(cb.equal(trainerJoin.get("userName"), trainerName));
            }
            if (trainingType != null && !trainingType.isEmpty()) {
                Join<TrainingEntity, TrainingTypeEntity> trainingTypeJoin = root.join("trainingType");
                predicates.add(cb.equal(trainingTypeJoin.get("trainingType"), trainingType));
            }

            cq.where(predicates.toArray(new Predicate[0]));

            transaction.commit();
            return session.createQuery(cq).getResultList();
        } catch (Exception e) {
            log.error(e.getMessage());
            if (transaction != null) transaction.rollback();
        }
        return new ArrayList<>();
    }
}
