package org.example.crmsystem.storage.interfaces;

import org.example.crmsystem.exception.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

public interface Storage<T> {
    T add(T entity);

    T update(T entity) throws EntityNotFoundException;

    Optional<T> getById(long id);

    boolean deleteById(long id);

    List<T> getByName(String name);

    List<T> getAll();
}
