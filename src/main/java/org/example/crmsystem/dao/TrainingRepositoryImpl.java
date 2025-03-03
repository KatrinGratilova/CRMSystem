package org.example.crmsystem.dao;

import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.example.crmsystem.dao.interfaces.TrainingDAO;
import org.example.crmsystem.dao.queries.TrainingQueries;
import org.example.crmsystem.entity.TraineeEntity;
import org.example.crmsystem.entity.TrainerEntity;
import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.messages.LogMessages;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Log4j2
@RequiredArgsConstructor
public class TrainingRepositoryImpl implements TrainingDAO {
    private final SessionFactory sessionFactory;

    @Override
    public TrainingEntity add(TrainingEntity trainingEntity) {
        String transactionId = ThreadContext.get("transactionId");
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
            transaction.commit();
        } catch (Exception e) {
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
            if (transaction != null) transaction.rollback();
        }
        return trainingEntity;
    }

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
}
