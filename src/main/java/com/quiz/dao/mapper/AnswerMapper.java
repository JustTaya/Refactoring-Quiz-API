package com.quiz.dao.mapper;

import com.quiz.entities.Answer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AnswerMapper implements RowMapper<Answer> {

    public static final String ID = "id";
    public static final String QUESTION_ID = "question_id";
    public static final String TEXT = "text";
    public static final String IMAGE = "image";
    public static final String CORRECT = "correct";
    public static final String NEXT_ANSWER_ID = "next_answer_id";

    @Override
    public Answer mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Answer answer = new Answer();

        answer.setId(resultSet.getInt(ID));
        answer.setQuestionId(resultSet.getInt(QUESTION_ID));
        answer.setText(resultSet.getString(TEXT));
        answer.setImage(resultSet.getString(IMAGE));
        answer.setCorrect(resultSet.getBoolean(CORRECT));
        answer.setNextAnswerId(resultSet.getInt(NEXT_ANSWER_ID));

        return answer;
    }
}
