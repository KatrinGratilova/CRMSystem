package org.example.crmsystem.dao;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.example.crmsystem.entity.TrainingTypeEntity;
import org.example.crmsystem.messages.LogMessages;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Log4j2
public class TrainingTypeRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public TrainingTypeRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public TrainingTypeEntity add(TrainingTypeEntity traineeEntity) {
        String transactionId = ThreadContext.get("transactionId");
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            if (traineeEntity.getId() == 0)
                session.persist(traineeEntity);
            else
                traineeEntity = session.merge(traineeEntity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.error(LogMessages.ERROR_OCCURRED.getMessage(), transactionId, e.getMessage());
        }
        return traineeEntity;
    }

    public TrainingTypeEntity getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(TrainingTypeEntity.class, id);
        }
    }

    public List<TrainingTypeEntity> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM TrainingTypeEntity", TrainingTypeEntity.class).list();
        }
    }
}
