package com.quiz.data.dto;

import com.quiz.data.entities.Answer;
import com.quiz.data.entities.Question;
import com.quiz.data.entities.QuestionType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class QuestionCheckDto {
    private int id;
    private int quizId;
    private QuestionType type;
    private String text;
    private boolean active;
    private int languageId;
    private List<Answer> answers;

    public QuestionCheckDto(Question question, List<Answer> answers) {
        this.id = question.getId();
        this.quizId = question.getQuizId();
        this.type = question.getType();
        this.text = question.getText();
        this.active = question.isActive();
        this.languageId = question.getLanguageId();
        this.answers = answers;
    }
}
