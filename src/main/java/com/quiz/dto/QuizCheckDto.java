package com.quiz.dto;

import com.quiz.entities.StatusType;
import com.quiz.service.QuestionService;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class QuizCheckDto{

    private int id;
    private String name;
    private int author;
    private int category_id;
    private Date date;
    private String description;
    private StatusType status;
    private Timestamp modificationTime;
    private String category;
    private String authorName;
    private String authorSurname;
    private String authorEmail;
    private List<QuestionCheckDto> questions;

    public QuizCheckDto(int id, String name, int author, int category_id, Date date, String description, StatusType status, Timestamp modificationTime, String category,String authorName,String authorSurname, String authorEmail) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.category_id = category_id;
        this.date = date;
        this.description = description;
        this.status = status;
        this.modificationTime = modificationTime;
        this.category=category;
        this.authorName=authorName;
        this.authorSurname=authorSurname;
        this.authorEmail=authorEmail;
    }
    public QuizCheckDto(QuizDto quizDto,List<QuestionCheckDto> questions){
        this.id = quizDto.getId();
        this.name = quizDto.getName();
        this.author = quizDto.getAuthor();
        this.category_id = quizDto.getCategory_id();
        this.date = quizDto.getDate();
        this.description = quizDto.getDescription();
        this.status = quizDto.getStatus();
        this.modificationTime = quizDto.getModificationTime();
        this.category=quizDto.getCategory();
        this.authorName=quizDto.getAuthorName();
        this.authorSurname=quizDto.getAuthorSurname();
        this.authorEmail=quizDto.getAuthorEmail();
        this.questions=questions;

    }
}
