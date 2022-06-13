package com.quiz.data.dao;

import com.quiz.data.dao.mapper.GameSessionMapper;
import com.quiz.data.dto.GameDto;
import com.quiz.data.dto.GameSessionDto;
import com.quiz.data.entities.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;


@RequiredArgsConstructor
@Repository
public class GameDao {
    private final JdbcTemplate jdbcTemplate;

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DATE = "date";
    public static final String SCORE = "score";

    public static final String COUNT = "count";
    public static final String MAX_USERS_NUMBER = "max_users_number";

    private static final String LIMIT_OFFSET = " LIMIT ? OFFSET ? ";
    private static final String INSERT_GAME = "INSERT INTO games (quiz_id, host_id, question_timer, date, max_users_number) VALUES (?, ?, ?, ?, ?)";
    private static final String GET_PLAYER_LIMIT = "SELECT max_users_number FROM games WHERE id =?";
    private static final String SAVE_SCORE = "INSERT INTO score (user_id, game_id, score) VALUES (?, ?, ?)";

    private static final String GET_GAME = "SELECT quiz_id, host_id, question_timer,max_users_number FROM games WHERE id = ?";
    private static final String GET_GAMES_BY_USER_ID = "SELECT games.id, quizzes.name, games.date, score FROM score " +
            "INNER JOIN games ON score.game_id = games.id " +
            "INNER JOIN quizzes ON quizzes.id = games.quiz_id " +
            "WHERE user_id = ?";
    private static final String COUNT_NUMBER_OF_PLAYED_GAMES = "SELECT COUNT(*) FROM score " +
            "INNER JOIN games ON score.game_id = games.id " +
            "INNER JOIN quizzes ON quizzes.id = games.quiz_id " +
            "WHERE user_id = ? ";
    private static final String GET_GAME_RESULT_BY_ID = "SELECT users.name || ' ' || users.surname AS name, score " +
            "FROM score INNER JOIN users " +
            "ON users.id = user_id " +
            "WHERE game_id = ? ";
    private static final String FILTER_PLAYED_GAMES = "SELECT games.id, quizzes.name, games.date, score FROM score " +
            "INNER JOIN games ON score.game_id = games.id " +
            "INNER JOIN quizzes ON quizzes.id = games.quiz_id " +
            "WHERE user_id = ? AND ((quizzes.name ~* ?) OR (games.date::text  ~* ?) OR (score::text   ~* ?))";

    public int insertGame(int quizId, int hostId, int questionTimer, int maxUsersNumber) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_GAME, new String[]{ID});
                    ps.setInt(1, quizId);
                    ps.setInt(2, hostId);
                    ps.setInt(3, questionTimer);
                    ps.setDate(4, Date.valueOf(LocalDate.now()));
                    ps.setInt(5, maxUsersNumber);
                    return ps;
                }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    public int getUserNumberByGameId(int gameId) {
        return jdbcTemplate.query(GET_PLAYER_LIMIT,
                new Object[]{gameId},
                (resultSet, i) -> resultSet.getInt(MAX_USERS_NUMBER)).get(0);
    }

    public void saveScore(int userId, int gameId, int score) {
        jdbcTemplate.update(SAVE_SCORE, userId, gameId, score);
    }

    public GameSessionDto getGame(int gameId) {
        return jdbcTemplate.queryForObject(GET_GAME, new Object[]{gameId}, new GameSessionMapper());
    }

    public List<GameDto> getPlayedGame(int userId, int limit, int offset, String sort) {
        List<GameDto> gameDtos = jdbcTemplate.query(
                sort.isEmpty() ? GET_GAMES_BY_USER_ID + LIMIT_OFFSET : GET_GAMES_BY_USER_ID + " ORDER BY " + sort + LIMIT_OFFSET,
                new Object[]{userId, limit, limit * offset},
                ((resultSet, i) -> new GameDto(resultSet.getInt(ID),
                        resultSet.getString(NAME),
                        resultSet.getDate(DATE),
                        resultSet.getInt(SCORE))));

        if (gameDtos.isEmpty()) {
            return Collections.emptyList();
        }

        return gameDtos;
    }

    public List<GameDto> getFilteredPlayedGame(int userId, int limit, int offset, String sort, String search) {
        List<GameDto> gameDtos = jdbcTemplate.query(
                sort.isEmpty() ? FILTER_PLAYED_GAMES + LIMIT_OFFSET : FILTER_PLAYED_GAMES + " ORDER BY " + sort + LIMIT_OFFSET,
                new Object[]{userId, search, search, search, limit, limit * offset},
                ((resultSet, i) -> new GameDto(resultSet.getInt(ID),
                        resultSet.getString(NAME),
                        resultSet.getDate(DATE),
                        resultSet.getInt(SCORE))));

        if (gameDtos.isEmpty()) {
            return Collections.emptyList();
        }

        return gameDtos;
    }

    public int getNumberOfRecord(int userId) {
        return jdbcTemplate.queryForObject(COUNT_NUMBER_OF_PLAYED_GAMES,
                new Object[]{userId},
                (resultSet, i) -> resultSet.getInt(COUNT));
    }

    public Set<Player> getGameResult(int gameId) {
        return new TreeSet<>(jdbcTemplate.query(GET_GAME_RESULT_BY_ID, new Object[]{gameId},
                ((resultSet, i) -> new Player(resultSet.getInt(SCORE),
                        resultSet.getString(NAME)
                ))));
    }
}
