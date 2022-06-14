package com.quiz.data.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.sql.Date;

@Data
@AllArgsConstructor
public class Announcement {
    private Date date;
    private String text;
    private boolean generated;
}
