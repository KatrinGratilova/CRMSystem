package org.example.crmsystem.exception;

import java.io.IOException;

public class RefreshTokenDoesNotExist extends IOException {
    public RefreshTokenDoesNotExist(String message) {
        super(message);
    }
}
