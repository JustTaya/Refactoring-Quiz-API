package com.quiz.dto;

import com.quiz.entities.AchievementCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AchievementCategoryDto {
    private int id;
    private String name;

    public AchievementCategoryDto(AchievementCategory achievementCategory) {
        this.id = achievementCategory.getId();
        this.name = achievementCategory.getName();
    }

    public AchievementCategoryDto(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
