package com.quiz.dao.mapper;

import com.quiz.dto.GameSessionDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GameSessionMapper implements RowMapper<GameSessionDto> {
    public static final String QUIZ_ID = "quiz_id";
    public static final String HOST_ID = "host_id";
    public static final String QUESTION_TIMER = "question_timer";
    public static final String MAX_USERS_NUMBER = "max_users_number";

    @Override
    public GameSessionDto mapRow(ResultSet resultSet, int i) throws SQLException {
        GameSessionDto game = new GameSessionDto();

        game.setQuizId(resultSet.getInt(QUIZ_ID));
        game.setHostId(resultSet.getInt(HOST_ID));
        game.setQuestionTimer(resultSet.getInt(QUESTION_TIMER));
        game.setMaxUsersNumber(resultSet.getInt(MAX_USERS_NUMBER));

        return game;
    }
}
