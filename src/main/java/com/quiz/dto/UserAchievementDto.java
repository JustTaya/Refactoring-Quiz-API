package com.quiz.dto;

import com.quiz.entities.UserAchievement;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
public class UserAchievementDto {
    private int userId;
    private int achievementId;
    private int progress;
    private Date date;

    public UserAchievementDto(UserAchievement userAchievement) {
        userId = userAchievement.getUserId();
        achievementId = userAchievement.getAchievementId();
        progress = userAchievement.getProgress();
        date = userAchievement.getDate();
    }

    public UserAchievementDto(int userId, int achievementId, int progress, Date date) {
        this.userId = userId;
        this.achievementId = achievementId;
        this.progress = progress;
        this.date = date;
    }
}
