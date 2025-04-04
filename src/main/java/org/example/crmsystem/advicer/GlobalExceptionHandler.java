package org.example.crmsystem.advicer;

import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import org.example.crmsystem.exception.IncompatibleSpecialization;
import org.example.crmsystem.exception.RefreshTokenDoesNotExist;
import org.example.crmsystem.messages.ExceptionMessages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleInvalidRequestException(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        if (errorMessages.size() == 1) {
            return new ResponseEntity<>(errorMessages.get(0), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleMissingRequestBody() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionMessages.INVALID_REQUEST_BODY.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(IncompatibleSpecialization.class)
    public ResponseEntity<String> handleIncompatibleSpecialization(IncompatibleSpecialization ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(RefreshTokenDoesNotExist.class)
    public ResponseEntity<String> handleRefreshTokenDoesNotExist(RefreshTokenDoesNotExist ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<String> handleJwtException(JwtException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionMessages.INVALID_TOKEN.getMessage());
    }
}
