package com.quiz.exceptions;

public class UserNotFoundException extends RuntimeException {

    private static final String NOT_FOUND_BY_PARAM = "User is not found by %s '%s'";

    public UserNotFoundException(String param, String value) {
        super(String.format(NOT_FOUND_BY_PARAM, param, value));
    }
}
