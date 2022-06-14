package com.quiz.exceptions;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super(message);
    }

    public static DatabaseException resourceSearchException(String resource, String parameter) {
        return new DatabaseException(String.format("Find %s by %s database error occurred", resource, parameter));
    }

    public static DatabaseException resourceSearchException(String resource, String... parameters) {
        return new DatabaseException(String.format("Find %s by %s database error occurred",
                resource,
                String.join(",", parameters)));
    }

    public static DatabaseException accessExceptionOnInsert(String resourceName) {
        return new DatabaseException(String.format("Database access exception while %s insert", resourceName));
    }
}
