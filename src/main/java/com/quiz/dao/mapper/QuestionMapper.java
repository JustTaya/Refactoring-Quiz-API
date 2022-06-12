package com.quiz.dao.mapper;

import com.quiz.entities.Question;
import com.quiz.entities.QuestionType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class QuestionMapper implements RowMapper<Question> {

    public static final String ID = "id";
    public static final String QUIZ_ID = "quiz_id";
    public static final String TYPE = "type";
    public static final String TEXT = "text";
    public static final String ACTIVE = "active";
    public static final String LANGUAGE_ID = "language_id";
    public static final String IMAGE = "image";

    @Override
    public Question mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Question question = new Question();

        question.setId(resultSet.getInt(ID));
        question.setQuizId(resultSet.getInt(QUIZ_ID));
        question.setType(QuestionType.valueOf(resultSet.getString(TYPE)));
        question.setImage(resultSet.getString(IMAGE));
        question.setText(resultSet.getString(TEXT));
        question.setActive(resultSet.getBoolean(ACTIVE));
        question.setLanguageId(resultSet.getInt(LANGUAGE_ID));

        return question;
    }
}
