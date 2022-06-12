package com.quiz.dao.mapper;

import com.quiz.entities.Achievement;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AchievementMapper implements RowMapper<Achievement> {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String CATEGORY_ID = "category_id";
    public static final String PROGRESS = "progress";

    @Override
    public Achievement mapRow(ResultSet resultSet, int i) throws SQLException {
        Achievement achievement = new Achievement();

        achievement.setId(resultSet.getInt(ID));
        achievement.setName(resultSet.getString(NAME));
        achievement.setDescription(resultSet.getString(DESCRIPTION));
        achievement.setCategoryId(resultSet.getInt(CATEGORY_ID));
        achievement.setProgress(resultSet.getInt(PROGRESS));

        return achievement;
    }
}
