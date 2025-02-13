package org.example.crmsystem.dao;

import jakarta.persistence.TypedQuery;
import lombok.extern.log4j.Log4j2;
import org.example.crmsystem.dao.interfaces.TrainingDAO;
import org.example.crmsystem.dao.queries.TrainingQueries;
import org.example.crmsystem.entity.TrainingEntity;
import org.example.crmsystem.exception.EntityNotFoundException;
import org.example.crmsystem.messages.ExceptionMessages;
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

    @Override
    public TrainingEntity add(TrainingEntity trainingEntity) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(trainingEntity);
            transaction.commit();
        } catch (Exception e) {
            log.error(e.getMessage());
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

    @Override
    public TrainingEntity update(TrainingEntity trainingEntity) throws EntityNotFoundException {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            long id = trainingEntity.getId();

            if (getById(id).isPresent() && id != 0) {
                session.merge(trainingEntity);
                if (trainingEntity.getTrainee() != null)
                    session.merge(trainingEntity.getTrainee());
            } else
                throw new EntityNotFoundException(ExceptionMessages.TRAINING_NOT_FOUND.format(id));
            transaction.commit();
        } catch (Exception e) {
            log.error(e.getMessage());
            if (transaction != null) transaction.rollback();
        }
        return trainingEntity;
    }
}
