package com.quiz.data.dto;

import com.quiz.data.entities.Player;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class GameSessionDto {
    private int quizId;
    private int hostId;
    private int questionTimer;
    private int maxUsersNumber;
    private List<Player> players;
}
