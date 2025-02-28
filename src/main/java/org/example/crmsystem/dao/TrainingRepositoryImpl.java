package org.example.crmsystem.dao;

import jakarta.persistence.TypedQuery;
import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.dao.interfaces.TrainingDAO;
import org.example.crmsystem.dao.queries.TrainingQueries;
import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.TrainingEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Log4j2
public class TrainingRepositoryImpl implements TrainingDAO {
    private final SessionFactory sessionFactory;

    @Autowired
    public TrainingRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

//    @Override
//    public TrainingEntity add(TrainingEntity trainingEntity) {
//        Transaction transaction = null;
//
//        try (Session session = sessionFactory.openSession()) {
//            transaction = session.beginTransaction();
//            session.persist(trainingEntity);
//            transaction.commit();
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            if (transaction != null) transaction.rollback();
//        }
//        return trainingEntity;
//    }

    @Override
    public Optional<TrainingEntity> getById(long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(TrainingEntity.class, id));
        }
    }

    @Override
    public List<TrainingEntity> getByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            TypedQuery<TrainingEntity> jpqlQuery = session.createQuery(TrainingQueries.GET_BY_NAME.getQuery(), TrainingEntity.class);
            jpqlQuery.setParameter("name", name + "%");
            return jpqlQuery.getResultList();
        }
    }

    @Override
    public TrainingEntity add(TrainingEntity trainingEntity) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            TraineeEntity trainee = session.find(TraineeEntity.class, trainingEntity.getTrainee().getId());
            TrainerEntity trainer = session.find(TrainerEntity.class, trainingEntity.getTrainer().getId());

            trainee.getTrainers().add(trainingEntity.getTrainer());
            trainer.getTrainees().add(trainingEntity.getTrainee());

            trainingEntity.setTrainee(session.merge(trainee));
            trainingEntity.setTrainer(session.merge(trainer));

            session.persist(trainingEntity);
//            session.persist(trainingEntity);

//            if (trainingEntity.getTrainee() != null)
//                session.merge(trainingEntity.getTrainee());

//            System.out.println(trainingEntity);
            transaction.commit();
        } catch (Exception e) {
            log.error(e.getStackTrace());
            if (transaction != null) transaction.rollback();
        }
        return trainingEntity;
    }
}
