package com.quiz.api.rest.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String description;
}
