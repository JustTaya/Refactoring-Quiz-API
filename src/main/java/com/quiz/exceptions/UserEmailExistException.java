package com.quiz.exceptions;

public class UserEmailExistException extends RuntimeException{
    public UserEmailExistException(String parameterName, Object parameterValue) {
        super(String.format("User with this '%s': %s already exists", parameterName, parameterValue));
    }
}
