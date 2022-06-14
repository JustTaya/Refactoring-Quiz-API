package com.quiz.data.dao;

import com.quiz.data.dao.mapper.AchievementCategoryMapper;
import com.quiz.data.dao.mapper.AchievementMapper;
import com.quiz.data.dao.mapper.UserAchievementMapper;
import com.quiz.data.entities.Achievement;
import com.quiz.data.entities.AchievementCategory;
import com.quiz.data.entities.UserAchievement;
import com.quiz.exceptions.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AchievementDao {
    private final JdbcTemplate jdbcTemplate;

    private static final String RESOURCE = "achievement";

    public static final String GET_ACHIEVEMENT_CATEGORIES = "SELECT id, name FROM achievement_categories";
    public static final String GET_ACHIEVEMENTS = "SELECT id, name, description, category_id FROM achievements";
    public static final String GET_ACHIEVEMENTS_BY_USER = "SELECT id, name, description, category_id, progress FROM achievements INNER JOIN users_achievements on achievements.id = users_achievements.achievement_id WHERE user_id = ?";
    public static final String GET_ACHIEVEMENTS_BY_CATEGORIES = "SELECT id, name, description, category_id FROM achievements WHERE category_id = ?";
    public static final String COUNT_TOTAL_ACHIEVEMENTS = "SELECT COUNT(*) FROM achievements";
    public static final String COUNT_ACHIEVEMENTS_OF_USER = "SELECT COUNT(achievement_id) FROM users_achievements WHERE user_id=? AND progress=100";
    public static final String GET_PROGRESS = "SELECT progress, date FROM users_achievements WHERE user_id=? AND achievement_id=?";


    public List<AchievementCategory> getAchievementCategories() {
        List<AchievementCategory> achievementCategories = jdbcTemplate.query(GET_ACHIEVEMENT_CATEGORIES, new AchievementCategoryMapper());
        if (achievementCategories.isEmpty()) {
            return Collections.emptyList();
        }
        return achievementCategories;
    }

    public List<Achievement> getAchievements() {
        List<Achievement> achievements = jdbcTemplate.query(GET_ACHIEVEMENTS, new AchievementMapper());
        if (achievements.isEmpty()) {
            return Collections.emptyList();
        }
        return achievements;
    }

    public List<Achievement> getAchievementsByCategory(int categoryId) {
        List<Achievement> achievements = jdbcTemplate.query(GET_ACHIEVEMENTS_BY_CATEGORIES, new Object[]{categoryId}, new AchievementMapper());
        if (achievements.isEmpty()) {
            return Collections.emptyList();
        }
        return achievements;
    }

    public List<Achievement> getAchievementsByUser(int userId) {
        List<Achievement> achievements = jdbcTemplate.query(GET_ACHIEVEMENTS_BY_USER, new Object[]{userId}, new AchievementMapper());
        if (achievements.isEmpty()) {
            return Collections.emptyList();
        }
        return achievements;
    }

    public Integer countAchievementsTotal() {
        return jdbcTemplate.queryForObject(COUNT_TOTAL_ACHIEVEMENTS, Integer.class);
    }

    public Integer countAchievementsForUser(int userId) {
        return jdbcTemplate.queryForObject(COUNT_ACHIEVEMENTS_OF_USER, new Object[]{userId}, Integer.class);
    }

    public UserAchievement getProgress(int userId, int achievementId) {
        List<UserAchievement> userAchievements;
        try {
            userAchievements = jdbcTemplate.query(GET_PROGRESS, new Object[]{userId, achievementId}, new UserAchievementMapper());
            if (userAchievements.isEmpty()) {
                return null;
            }
        } catch (DataAccessException e) {
            throw DatabaseException.resourceSearchException(RESOURCE, "'userId': " + userId, "'achievementId': " + achievementId);
        }

        return userAchievements.get(0);
    }
}
