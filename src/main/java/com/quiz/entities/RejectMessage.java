package com.quiz.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

@Data
@AllArgsConstructor
public class RejectMessage {
    String comment;
    Date date;
}
