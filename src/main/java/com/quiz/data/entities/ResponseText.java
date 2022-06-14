package com.quiz.data.entities;

import lombok.Data;

@Data
public class ResponseText {
    String text;

    public ResponseText(String text) {
        this.text = text;
    }
}
