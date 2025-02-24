package org.example.crmsystem.exception;

import java.io.IOException;

public class UserIsNotAuthenticated extends IOException {
    public UserIsNotAuthenticated(String message) {
        super(message);
    }
}
