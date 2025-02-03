package org.example.crmsystem.exception;

import java.io.IOException;

public class EntityNotFoundException extends IOException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
