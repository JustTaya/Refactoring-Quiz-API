package com.quiz.data.dao.mapper;

import com.quiz.data.entities.AchievementCategory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AchievementCategoryMapper implements RowMapper<AchievementCategory> {
    public static final String ID = "id";
    public static final String NAME = "name";


    @Override
    public AchievementCategory mapRow(ResultSet resultSet, int i) throws SQLException {
        AchievementCategory achievementCategory = new AchievementCategory();

        achievementCategory.setId(resultSet.getInt(ID));
        achievementCategory.setName(resultSet.getString(NAME));

        return achievementCategory;
    }
}
