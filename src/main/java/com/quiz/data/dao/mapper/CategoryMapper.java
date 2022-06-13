package com.quiz.data.dao.mapper;

import com.quiz.data.entities.Category;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryMapper implements RowMapper<Category> {
    public static final String ID = "id";
    public static final String NAME = "name";

    @Override
    public Category mapRow(ResultSet resultSet, int i) throws SQLException {
        Category category = new Category();

        category.setId(resultSet.getInt(ID));
        category.setName(resultSet.getString(NAME));

        return category;
    }
}
