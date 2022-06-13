package com.quiz.exceptions;

public class GameSessionFullException extends RuntimeException {
    public GameSessionFullException(int gameId) {
        super(String.format("The session with id %s is already full", gameId));
    }
}
