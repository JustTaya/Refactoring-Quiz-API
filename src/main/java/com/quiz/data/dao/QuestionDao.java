package com.quiz.data.dao;

import com.quiz.data.dto.AnswerDto;
import com.quiz.data.dto.QuestionDto;
import com.quiz.data.entities.Question;
import com.quiz.data.entities.QuestionType;
import com.quiz.exceptions.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;

import static com.quiz.data.dao.mapper.QuestionMapper.*;

@Repository
@RequiredArgsConstructor
public class QuestionDao {
    private final JdbcTemplate jdbcTemplate;
    private final AnswerDao answerDao;

    private static final String RESOURCE = "question";

    private static final String QUESTION_FIND_BY_ID = "SELECT id, quiz_id, type, text, active FROM questions WHERE id = ?";
    private static final String QUESTION_FIND_BY_QUIZ_ID = "SELECT id, quiz_id, type, text, active FROM questions WHERE quiz_id = ?";
    private static final String QUESTION_IMAGE_BY_QUESTION_ID = "SELECT image from questions WHERE id = ?";

    private static final String INSERT_QUESTION = "INSERT INTO questions (quiz_id, type, text, active, image) VALUES ( ?, ?::question_type, ?,?,?)";

    private static final String UPDATE_QUESTION = "UPDATE questions SET type=?, text=?, active=?, image=? WHERE id=?";
    private static final String GET_QUESTIONS_BY_QUIZ_ID = "SELECT id, quiz_id, type, text, image, active FROM questions WHERE quiz_id =? AND active=true";

    private static final String DELETE_QUESTION = "DELETE FROM questions WHERE id = ?";

    public static final String TABLE_QUESTIONS = "questions";

    public Question findById(int id) {
        List<Question> questions;

        try {
            questions = getQuery(QUESTION_FIND_BY_ID, id);
            if (questions.isEmpty()) {
                return null;
            }

        } catch (DataAccessException e) {
            throw DatabaseException.resourceSearchException(RESOURCE, "'questionId': " + id);
        }

        return questions.get(0);
    }

    public List<Question> getQuery(String sql, int id) {
        return jdbcTemplate.query(
                sql,
                new Object[]{id},
                (resultSet, i) -> {
                    Question question = new Question();

                    question.setId(resultSet.getInt(ID));
                    question.setQuizId(resultSet.getInt(QUIZ_ID));
                    question.setType(QuestionType.valueOf(resultSet.getString(TYPE)));
                    question.setText(resultSet.getString(TEXT));
                    question.setActive(resultSet.getBoolean(ACTIVE));

                    return question;
                }
        );
    }

    public List<Question> findQuestionsByQuizId(int id) {
        return getQuery(QUESTION_FIND_BY_QUIZ_ID, id);
    }

    public byte[] getQuestionImageByQuestionId(int questionId) {
        List<byte[]> imageBlob = jdbcTemplate.query(
                QUESTION_IMAGE_BY_QUESTION_ID,
                new Object[]{questionId},
                (resultSet, i) -> resultSet.getBytes("image"));

        return imageBlob.get(0);
    }

    @Transactional
    public void insert(QuestionDto entity, int quizId) {

        entity.setQuizId(quizId);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                        PreparedStatement ps = connection
                                .prepareStatement(INSERT_QUESTION, new String[]{"id"});
                        ps.setInt(1, entity.getQuizId());
                        ps.setString(2, String.valueOf(entity.getType()));
                        ps.setString(3, entity.getText());
                        ps.setBoolean(4, entity.isActive());
                        ps.setString(5, entity.getImage());
                        return ps;
                    },
                    keyHolder
            );

        } catch (DataAccessException e) {
            throw DatabaseException.accessExceptionOnInsert(RESOURCE);
        }

        if (keyHolder.getKey() == null) return;

        for (int i = entity.getAnswerList().size() - 1; i >= 0; i--) {
            if (entity.getType() == QuestionType.SEQUENCE && i != entity.getAnswerList().size() - 1) {
                entity.getAnswerList().get(i).setNextAnswerId(entity.getAnswerList().get(i + 1).getId());
            }
            answerDao.insert(entity.getAnswerList().get(i), keyHolder.getKey().intValue());
        }

        entity.setId(keyHolder.getKey().intValue());
    }

    @Transactional
    public void update(QuestionDto entity, int quizId) {
        if (entity.getId() == null) {
            insert(entity, quizId);
        } else if (entity.isDeleted()) {
            delete(entity);
        } else if (entity.isChanged()) {
            jdbcTemplate.update(UPDATE_QUESTION,
                    entity.getType(),
                    entity.getText(),
                    entity.isActive(),
                    entity.getImage(),
                    entity.getId());
        }

        if (entity.isChangedType()) {
            answerDao.deleteAnswersByQuestionId(entity.getId());
        } else {
            entity.getAnswerList().removeIf(item -> {
                if (item.isDeleted()) {
                    answerDao.delete(item);
                    return true;
                }
                return false;
            });
        }
        for (int i = entity.getAnswerList().size() - 1; i >= 0; i--) {
            if (entity.getType() == QuestionType.SEQUENCE && i != entity.getAnswerList().size() - 1) {
                entity.getAnswerList().get(i).setNextAnswerId(entity.getAnswerList().get(i + 1).getId());
            }
            answerDao.update(entity.getAnswerList().get(i), entity.getId());
        }
    }

    @Transactional
    public void delete(QuestionDto entity) {
        for (AnswerDto answer : entity.getAnswerList()) {
            answerDao.delete(answer);
        }
        jdbcTemplate.update(DELETE_QUESTION,
                entity.getId()
        );
    }

    public List<Question> getQuestionsByQuizId(int quizId) {
        return jdbcTemplate.query(
                GET_QUESTIONS_BY_QUIZ_ID,
                new Object[]{quizId},
                (resultSet, i) -> {
                    Question question = new Question();
                    question.setId(resultSet.getInt(ID));
                    question.setType(QuestionType.valueOf(resultSet.getString(TYPE)));
                    question.setText(resultSet.getString(TEXT));
                    question.setImage(resultSet.getString(IMAGE));

                    return question;
                }
        );
    }

    public List<QuestionDto> getQuestionInfoByQuizId(int quizId) {
        return jdbcTemplate.query(GET_QUESTIONS_BY_QUIZ_ID,
                new Object[]{quizId},
                (resultSet, i) -> {
                    QuestionDto questionDto = new QuestionDto();
                    questionDto.setId(resultSet.getInt(ID));
                    questionDto.setQuizId(resultSet.getInt(QUIZ_ID));
                    questionDto.setType(QuestionType.valueOf(resultSet.getString(TYPE)));
                    questionDto.setText(resultSet.getString(TEXT));
                    questionDto.setImage(resultSet.getString("image"));
                    questionDto.setActive(resultSet.getBoolean(ACTIVE));

                    return questionDto;
                }
        );
    }
}
