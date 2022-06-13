package com.quiz.data.dao.mapper;

import com.quiz.data.entities.Quiz;
import com.quiz.data.entities.StatusType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class QuizMapper implements RowMapper<Quiz> {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String IMAGE = "image";
    public static final String AUTHOR = "author";
    public static final String CATEGORY_ID = "category_id";
    public static final String DATE = "date";
    public static final String DESCRIPTION = "description";
    public static final String STATUS = "status";
    public static final String MODIFICATION_TIME = "modification_time";
    public static final String CATEGORY = "category";

    @Override
    public Quiz mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Quiz quiz = new Quiz();
        quiz.setId(resultSet.getInt(ID));
        quiz.setName(resultSet.getString(NAME));
        quiz.setImage(resultSet.getString(IMAGE));
        quiz.setAuthor(resultSet.getInt(AUTHOR));
        quiz.setCategoryId(resultSet.getInt(CATEGORY_ID));
        quiz.setDate(resultSet.getDate(DATE));
        quiz.setDescription(resultSet.getString(DESCRIPTION));
        quiz.setStatus(StatusType.valueOf(resultSet.getString(STATUS)));
        quiz.setModificationTime(resultSet.getTimestamp(MODIFICATION_TIME));
        quiz.setCategory(resultSet.getString(CATEGORY));

        return quiz;
    }
}
