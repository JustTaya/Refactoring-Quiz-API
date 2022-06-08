package com.quiz.entities;

import lombok.Data;

@Data
public class ResponseToken {
    String token;

    String id;
    String email;
    String role;

    public ResponseToken(String token, String id, String email, String role) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.role = role;
    }
}
