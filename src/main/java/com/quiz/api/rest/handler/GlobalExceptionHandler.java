package com.quiz.api.rest.handler;

import com.quiz.api.rest.response.ErrorResponse;
import com.quiz.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorResponse> databaseError(DatabaseException e) {
        log.debug("Exception handled", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Database error", e.getMessage()));
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<ErrorResponse> emailAlreadyExists(EmailExistException e) {
        log.debug("Exception handled", e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Email already exists", e.getMessage()));
    }

    @ExceptionHandler(GameSessionFullException.class)
    public ResponseEntity<ErrorResponse> gameSessionAlreadyFull(GameSessionFullException e) {
        log.debug("Exception handled", e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Game Session is already full", e.getMessage()));
    }

    @ExceptionHandler(PasswordException.class)
    public ResponseEntity<ErrorResponse> incorrectUserPassword(PasswordException e) {
        log.debug("Exception handled", e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("User password is not correct", e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> incorrectUserPassword(UserNotFoundException e) {
        log.debug("Exception handled", e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("User is not found", e.getMessage()));
    }
}
