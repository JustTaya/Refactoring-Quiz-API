package com.quiz.dto;

import com.quiz.entities.ModeratorComment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
public class ModeratorCommentDto {

    private int id;
    private int moderatorId;
    private int quizId;
    private Date commentDate;
    private String comment;
    private String moderatorName;
    private String moderatorSurname;
    private String moderatorEmail;

    public ModeratorCommentDto(ModeratorComment moderatorComment, String moderatorName, String moderatorSurname, String moderatorEmail){
        this.id= moderatorComment.getId();
        this.quizId= moderatorComment.getQuizId();
        this.moderatorId= moderatorComment.getModeratorId();
        this.comment= moderatorComment.getComment();
        this.commentDate= moderatorComment.getCommentDate();
        this.moderatorName=moderatorName;
        this.moderatorEmail=moderatorEmail;
        this.moderatorSurname=moderatorSurname;

    }
}
