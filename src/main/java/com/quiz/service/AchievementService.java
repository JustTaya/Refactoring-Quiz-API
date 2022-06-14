package com.quiz.service;

import com.quiz.data.dao.AchievementDao;
import com.quiz.data.entities.Achievement;
import com.quiz.data.entities.AchievementCategory;
import com.quiz.data.entities.UserAchievement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementService {
    private final AchievementDao achievementDao;

    public List<AchievementCategory> getAchievementCategories() {
        return achievementDao.getAchievementCategories();
    }

    public List<Achievement> getAchievements() {
        return achievementDao.getAchievements();
    }

    public List<Achievement> getAchievementsByUser(int userId) {
        return achievementDao.getAchievementsByUser(userId);
    }


    public List<Achievement> findAchievementByCategory(int categoryId) {
        return achievementDao.getAchievementsByCategory(categoryId);
    }

    public Integer countAchievementsTotal() {
        return achievementDao.countAchievementsTotal();
    }

    public Integer countAchievementsForUser(int userId) {
        return achievementDao.countAchievementsForUser(userId);
    }

    public UserAchievement getProgress(int userId, int achievementId) {
        return achievementDao.getProgress(userId, achievementId);
    }


}
