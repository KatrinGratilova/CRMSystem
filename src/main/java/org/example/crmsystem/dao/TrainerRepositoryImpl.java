package org.example.crmsystem.dao;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.dao.interfaces.TrainerDAO;
import org.example.crmsystem.dao.queries.TrainerQueries;
import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.messages.ExceptionMessages;
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
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(trainerEntity);
            transaction.commit();
        } catch (Exception e) {
            log.error(e.getMessage());
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
    public Optional<TrainerEntity> getByUserName(String userName) {
        try (Session session = sessionFactory.openSession()) {
            Query<TrainerEntity> query = session.createQuery(TrainerQueries.GET_BY_USERNAME.getQuery(), TrainerEntity.class);
            query.setParameter("userName", userName);

            List<TrainerEntity> resultList = query.getResultList();
            System.out.println(resultList);
            return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
        }
    }

    @Override
    public List<TrainerEntity> getWhereUserNameStartsWith(String userName) {
        try (Session session = sessionFactory.openSession()) {
            Query<TrainerEntity> query = session.createQuery(TrainerQueries.GET_WHERE_USERNAME_STARTS_WITH.getQuery(), TrainerEntity.class);
            query.setParameter("userName", userName + "%");

            return query.getResultList();
        }
    }

    @Override
    public TrainerEntity update(TrainerEntity trainerModified) throws EntityNotFoundException {
        Transaction transaction = null;
        TrainerEntity trainer = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            long id = trainerModified.getId();

            Optional<TrainerEntity> trainerOptional = getByUserName(trainerModified.getUserName());

            if (trainerOptional.isPresent()) {
                trainer = trainerOptional.get();

                trainer.setFirstName(trainerModified.getFirstName());
                trainer.setLastName(trainerModified.getLastName());
                trainer.setSpecialization(trainerModified.getSpecialization());
                trainer.setActive(trainerModified.isActive());

                session.merge(trainer);
            } else
                throw new EntityNotFoundException(ExceptionMessages.TRAINER_NOT_FOUND.format(id));
            transaction.commit();
        } catch (Exception e) {
            log.error(e.getMessage());
            if (transaction != null) transaction.rollback();
        }
        return trainer;
    }

    @Override
    public TrainerEntity updatePassword(TrainerEntity trainerEntity) throws EntityNotFoundException {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            long id = trainerEntity.getId();

            Optional<TrainerEntity> traineeOptional = getByUserName(trainerEntity.getUserName());

            if (traineeOptional.isPresent()) {
                TrainerEntity savedTrainee = traineeOptional.get();

                trainerEntity.setId(savedTrainee.getId());
                session.merge(trainerEntity);
            }
            else
                throw new EntityNotFoundException(ExceptionMessages.TRAINEE_NOT_FOUND.format(id));
            transaction.commit();
        } catch (Exception e) {
            log.error(e.getMessage());
            if (transaction != null) transaction.rollback();
        }
        return trainerEntity;
    }

    @Override
    public boolean toggleActiveStatus(String username, boolean isActive) throws EntityNotFoundException {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Optional<TrainerEntity> trainerOptional = getByUserName(username);
            TrainerEntity trainer;
            //TrainerEntity trainee = session.find(TrainerEntity.class, trainerEntity.getId());
            if (trainerOptional.isPresent()) {
                trainer = trainerOptional.get();
                trainer.setActive(isActive);
                session.merge(trainer);

                transaction.commit();
                return true;
            } else {
                throw new EntityNotFoundException(ExceptionMessages.TRAINER_NOT_FOUND_BY_USERNAME.format(username));
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            if (transaction != null) transaction.rollback();
            return false;
        }
    }

//    @Override
//    public List<TrainerEntity> getTrainersNotAssignedToTrainee(String traineeUserName) {
//        Transaction transaction = null;
//        List<TrainerEntity> result = new ArrayList<>();
//
//        try (Session session = sessionFactory.openSession()) {
//            transaction = session.beginTransaction();
//
//            TypedQuery<TrainerEntity> query = session.createQuery(TrainerQueries.GET_TRAINERS_NOT_ASSIGNED_TO_TRAINEE.getQuery(), TrainerEntity.class);
//            query.setParameter("traineeUserName", traineeUserName);
//
//            result = query.getResultList();
//            transaction.commit();
//        } catch (Exception e) {
//            System.out.println(1);
//            if (transaction != null) transaction.rollback();
//            log.error(e.getMessage());
//        }
//
//        return result;
//    }

    public List<TrainingEntity> getTrainerTrainingsByCriteria(
            String trainerUserName, LocalDateTime fromDate, LocalDateTime toDate, String traineeName) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<TrainingEntity> cq = cb.createQuery(TrainingEntity.class);
            Root<TrainingEntity> root = cq.from(TrainingEntity.class);

            List<Predicate> predicates = new ArrayList<>();

            Join<TrainingEntity, TrainerEntity> trainerJoin = root.join("trainer");
            predicates.add(cb.equal(trainerJoin.get("userName"), trainerUserName));

            if (fromDate != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("trainingDate"), fromDate));
            if (toDate != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("trainingDate"), toDate));

            if (traineeName != null && !traineeName.isEmpty()) {
                Join<TrainingEntity, TraineeEntity> traineeJoin = root.join("trainee");
                predicates.add(cb.equal(traineeJoin.get("userName"), traineeName));
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
