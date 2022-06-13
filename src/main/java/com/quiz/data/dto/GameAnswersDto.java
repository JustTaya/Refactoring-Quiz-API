package com.quiz.data.dto;

import com.quiz.data.entities.Answer;
import com.quiz.data.entities.Player;
import lombok.Data;

import java.util.List;

@Data
public class GameAnswersDto {
    private List<Answer> answers;
    private Player player;
}
