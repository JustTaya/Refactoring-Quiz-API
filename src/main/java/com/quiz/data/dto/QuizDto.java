package com.quiz.data.dto;

import com.quiz.data.entities.StatusType;
import com.quiz.data.entities.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class QuizDto {
    @NotNull
    private Integer id;
    private String name;
    private String image;
    private int author;
    private int categoryId;
    private Date date;
    private String description;
    private StatusType status;
    private Timestamp modificationTime;
    private String category;
    private String authorName;
    private String authorSurname;
    private String authorEmail;
    private List<QuestionDto> questions;
    private List<Tag> tags;
    private boolean favorite;
    private boolean changed;
    private String moderatorComment;
}
