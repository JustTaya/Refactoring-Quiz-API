package com.quiz.data.entities;

import lombok.*;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Player implements Comparable<Player> {
    int userId;
    int userScore;
    String userName;
    boolean authorize;

    public Player(int userId, String userName, boolean authorize) {
        this.userId = userId;
        this.userScore = 0;
        this.userName = userName;
        this.authorize = authorize;
    }

    public Player(int score, String userName) {
        this.userScore = score;
        this.userName = userName;
    }

    @Override
    public int compareTo(Player player) {
        int result = Integer.compare(player.userScore, this.userScore);
        return result == 0 ? Integer.compare(this.userId, player.getUserId()) : result;
    }
}
