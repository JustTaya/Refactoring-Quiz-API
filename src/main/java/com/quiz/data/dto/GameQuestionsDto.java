package com.quiz.data.dto;

import com.quiz.data.entities.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameQuestionsDto {
    int questionNumber;
    int questionTimer;
    Question question;
}
