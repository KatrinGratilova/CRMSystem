package org.example.crmsystem.dao;

import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.entity.TrainingTypeEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Log4j2
public class TrainingTypeRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public TrainingTypeRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public TrainingTypeEntity add(TrainingTypeEntity traineeEntity) {
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
            log.error(e.getMessage());
        }
        return traineeEntity;
    }

    public TrainingTypeEntity getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(TrainingTypeEntity.class, id);
        }
    }
}
