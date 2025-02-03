package org.example.crmsystem.storage.interfaces;

import org.example.crmsystem.exception.EntityNotFoundException;

public interface TrainingAttending <T>{
    void addTraining(long trainerId, long trainingId) throws EntityNotFoundException;
}
