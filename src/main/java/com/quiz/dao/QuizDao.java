package com.quiz.dao;

import com.quiz.converters.QuizConverter;
import com.quiz.dao.mapper.QuizMapper;
import com.quiz.dto.ModeratorCommentDto;
import com.quiz.dto.QuestionDto;
import com.quiz.dto.QuizDto;
import com.quiz.entities.*;
import com.quiz.exceptions.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.quiz.dao.mapper.QuizMapper.*;


@Repository
@RequiredArgsConstructor
public class QuizDao {

    private final JdbcTemplate jdbcTemplate;
    private final QuestionDao questionDao;
    private final AnswerDao answerDao;
    private final TagDao tagDao;
    private final QuizConverter quizConverter;

    public static final String QUIZ_ID = "quiz_id";
    public static final String QUIZ_NAME = "quizName";
    public static final String QUIZ_IMAGE = "quizImage";
    public static final String QUIZ_AUTHOR = "quizAuthor";
    public static final String QUIZ_DATE = "quizDate";
    public static final String QUIZ_DESCRIPTION = "quizDescription";
    public static final String QUIZ_CATEGORY_ID = "quizCategoryId";
    public static final String QUIZ_STATUS = "quizStatus";
    public static final String QUIZ_MOD_TIME = "quizModTime";

    public static final String AUTHOR_ID = "authorId";
    public static final String AUTHOR_NAME = "authorName";
    public static final String AUTHOR_SURNAME = "authorSurname";
    public static final String AUTHOR_EMAIL = "authorEmail";

    public static final String COMMENT_TEXT = "commentText";
    public static final String COMMENT_DATE = "commentDate";

    public static final String MODERATOR_ID = "moderatorId";
    public static final String MODERATOR_NAME = "moderatorName";
    public static final String MODERATOR_SURNAME = "moderatorSurname";
    public static final String MODERATOR_EMAIL = "moderatorEmail";

    public static final String QUIZ_SHORT_ID = "qid";
    public static final String QUIZ_SHORT_NAME = "qname";
    public static final String QUIZ_SHORT_DATE = "qdate";
    public static final String QUIZ_SHORT_CATEGORY_ID = "qcategoryid";
    public static final String QUIZ_SHORT_STATUS = "qstatus";
    public static final String QUIZ_SHORT_AUTHOR = "qauthor";
    public static final String QUIZ_SHORT_DESCRIPTION = "qdescription";
    public static final String QUIZ_MODIFICATION_TIME = "qmodificationtime";

    public static final String CATEGORY_SHORT_NAME = "cname";

    public static final String USER_SHORT_NAME = "uname";
    public static final String USER_SHORT_SURNAME = "usurname";
    public static final String USER_SHORT_EMAIL = "uemail";

    public static final String COMMENT = "comment";
    public static final String COUNT = "count";


    private static final String GET_QUIZZES_BY_STATUS_NAME = "SELECT * FROM (SELECT quizzes.category_id quizCategoryId, quizzes.modification_time quizModTime, quizzes.date quizDate, quizzes.description quizDescription, quizzes.status quizStatus, quizzes.id id, quizzes.name quizName, date quizDate, categories.name AS category, users.name AS authorName,users.id authorId, users.surname AS authorSurname, users.email AS authorEmail FROM quizzes INNER JOIN categories ON categories.id = category_id INNER JOIN users ON quizzes.author = users.id WHERE quizzes.status = ?::status_type) quizzes WHERE id NOT IN (SELECT quiz_id FROM moderators_quizzes)";
    private static final String GET_QUIZ_BY_ID_NAME = "SELECT sq.qid qid,sq.qauthor qauthor, u.id uid, u.name uname," +
            " u.surname usurname, u.email uemail, sq.qdate qdate,sq.qdescription qdescription,sq.qimage qimage," +
            " sq.qmodificationtime qmodificationtime, sq.qname qname, sq.cname cname, sq.qcategoryid qcategoryid," +
            " sq.qstatus qstatus" +
            " FROM (SELECT q.id qid,q.author qauthor,q.date qdate,q.description qdescription,q.image qimage," +
            " q.modification_time qmodificationtime, q.name qname, q.category_id qcategoryid," +
            " q.status qstatus, c.name cname " +
            "FROM quizzes q INNER JOIN categories c on q.category_id = c.id where q.id = ?) sq INNER JOIN users u on sq.qauthor = u.id";

    private static final String GET_ALL_QUIZZES = "SELECT quizzes.id, quizzes.name, image, author, category_id, date, description, status, modification_time, categories.id, categories.name AS category FROM quizzes INNER JOIN categories ON categories.id = category_id WHERE quizzes.status = 'ACTIVE' LIMIT ? OFFSET ?";
    private static final String GET_QUIZZES_CREATED_BY_USER_ID = "SELECT quizzes.id, quizzes.name, image, author, category_id, date, description, status, modification_time, categories.id, categories.name AS category " +
            "FROM quizzes INNER JOIN categories ON categories.id = category_id " +
            "WHERE author = ? AND (status<>'DELETED' AND status<>'DEACTIVATED')";
    private static final String GET_QUIZ_BY_ID = "SELECT id, name, image, author, category_id, date, description, status, modification_time FROM quizzes WHERE id = ?";
    private static final String GET_FAVORITE_QUIZZES_BY_USER_ID = "SELECT quizzes.id, quizzes.name, image, author," +
            " category_id, date, description, status, modification_time," +
            " categories.id, categories.name AS category " +
            "FROM quizzes INNER JOIN categories " +
            "ON categories.id = category_id " +
            "INNER JOIN favorite_quizzes " +
            "ON quizzes.id = quiz_id WHERE user_id = ?";
    private static final String GET_QUIZZES_BY_CATEGORY_ID = "SELECT quizzes.id, quizzes.name, image, author, category_id, date, description, status, modification_time, categories.id, categories.name AS category FROM quizzes INNER JOIN categories ON categories.id = category_id WHERE (category_id = ?) AND (quizzes.status = 'ACTIVE')";
    private static final String GET_QUIZZES_BY_TAG = "SELECT * FROM quizzes INNER JOIN quizzes_tags on id = quiz_id where tag_id = ?";
    private static final String GET_QUIZZES_BY_NAME = "SELECT * FROM quizzes WHERE name LIKE ?";
    private static final String GET_QUIZ_IMAGE_BY_QUIZ_ID = "SELECT image FROM quizzes WHERE id = ?";
    private static final String INSERT_QUIZ = "INSERT INTO quizzes (name , author, category_id, date, description,status, modification_time,image) VALUES (?,?,?,CURRENT_DATE,?,?::status_type,?,?)";
    private static final String ADD_TAG_TO_QUIZ = "INSERT INTO quizzes_tags (quiz_id, tag_id) VALUES (?,?)";
    private static final String UPDATE_QUIZ = "UPDATE quizzes SET name = ?, author = ?, category_id = ?, date = ?, description = ?, status = ?::status_type, modification_time = ?, image = ? WHERE id = ?";
    private static final String GET_FILTERED_QUIZZES = "SELECT quizzes.id, quizzes.name, quizzes.image, author, category_id, date, description, status, modification_time, categories.id, categories.name AS category, users.name AS authorName, users.surname AS authorSurname FROM quizzes INNER JOIN categories ON categories.id = category_id INNER JOIN users ON quizzes.author = users.id WHERE quizzes.name ~* ? OR categories.name ~* ? OR CONCAT(users.name, ' ', surname) ~*? OR date::text ~* ?";
    private static final String GET_POPULAR_QUIZ = "SELECT quizzes.id, quizzes.name, image, author, category_id, date, description, status, modification_time, categories.id, categories.name AS category, COUNT(quiz_id)  AS counter FROM quizzes INNER JOIN categories ON categories.id = category_id INNER JOIN favorite_quizzes ON quizzes.id = favorite_quizzes.quiz_id WHERE quizzes.status = 'ACTIVE' GROUP BY quizzes.id, categories.id ORDER BY counter DESC LIMIT ?";
    private static final String FILTER_QUIZZES_CREATED_BY_USER = "SELECT quizzes.id, quizzes.name, image, author, category_id, date, description, status, modification_time, categories.id, categories.name AS category FROM quizzes INNER JOIN categories ON categories.id = category_id WHERE author = ? AND (status<>'DELETED' AND status<>'DEACTIVATED') AND (quizzes.name ~* ? OR categories.name ~* ? OR date::text ~* ?)";
    private static final String FILTER_FAVORITE_QUIZZES = "SELECT quizzes.id, quizzes.name, image, author, category_id, date, description, status, modification_time, categories.id, categories.name AS category FROM quizzes INNER JOIN categories ON categories.id = category_id INNER JOIN favorite_quizzes ON quizzes.id = quiz_id WHERE user_id = ? AND (quizzes.name ~* ? OR categories.name ~* ? OR CONCAT(name, ' ', surname) ~*? OR date::text ~* ?)";

    private static final String IS_FAVORITE_QUIZ = "select * from favorite_quizzes WHERE quiz_id = ? AND user_id = ?";
    private static final String MARK_QUIZ_AS_FAVORITE = "INSERT INTO favorite_quizzes (user_id, quiz_id) VALUES(?, ?) ";
    private static final String UNMARK_QUIZ_AS_FAVORITE = "DELETE FROM favorite_quizzes where user_id = ? AND quiz_id = ?";
    private static final String GET_TAGS_BY_QUIZ_ID = "select name from tags INNER JOIN quizzes_tags ON tags.id = quizzes_tags.tag_id WHERE quizzes_tags.quiz_id = ?";

    private static final String GET_QUIZ_RECOMMENDATIONS = "SELECT quizzes.id, quizzes.name, quizzes.image, quizzes.author, quizzes.category_id, quizzes.date,quizzes.description, quizzes.status, quizzes.modification_time, categories.name AS category, COUNT(games.id) AS count_games_general, count_games FROM quizzes INNER JOIN games ON quizzes.id = games.quiz_id INNER JOIN favorite_categories(?) ON quizzes.category_id=favorite_categories.category_id INNER JOIN categories ON quizzes.category_id = categories.id WHERE quizzes.status='ACTIVE' GROUP BY quizzes.id,favorite_categories.count_games, categories.name ORDER BY count_games_general DESC , count_games DESC LIMIT ?";
    private static final String GET_QUIZ_RECOMMENDATIONS_BY_FRIENDS = "SELECT quizzes.id, quizzes.name, quizzes.author, quizzes.category_id, quizzes.date, quizzes.description, quizzes.status, quizzes.modification_time, COUNT(games.id) AS gamescount FROM games INNER JOIN quizzes on games.quiz_id=quizzes.id WHERE games.id IN(SELECT games.quiz_id FROM games WHERE games.id IN (SELECT games.id FROM score WHERE score.user_id IN (SELECT friend_id FROM friends WHERE friends.user_id = ?) ) ) AND quizzes.status = 'ACTIVE' GROUP BY quizzes.id ORDER BY gamescount DESC LIMIT ?";


    //Functionality for dashboard
    private static final String GET_TOP_POPULAR_QUIZZES = "SELECT quizzes.id, quizzes.name, quizzes.author, quizzes.category_id, quizzes.date, quizzes.description, quizzes.status, quizzes.modification_time, COUNT(games.id) AS gamescount FROM games INNER JOIN quizzes ON games.quiz_id = quizzes.id GROUP BY quizzes.id ORDER BY gamescount DESC LIMIT ?";
    private static final String GET_TOP_POPULAR_QUIZZES_BY_CATEGORY = "SELECT quizzes.id, quizzes.name, quizzes.author, quizzes.category_id, quizzes.date, quizzes.description, quizzes.status, quizzes.modification_time, COUNT(games.id) AS gamescount FROM games INNER JOIN quizzes ON games.quiz_id = quizzes.id WHERE category_id=? GROUP BY quizzes.id ORDER BY gamescount DESC LIMIT ?";
    private static final String GET_RECENT_GAMES = "SELECT quizzes.id, quizzes.name, quizzes.author, quizzes.category_id, quizzes.date, quizzes.description, quizzes.status, quizzes.modification_time FROM games INNER JOIN quizzes ON games.quiz_id = quizzes.id WHERE games.id IN (SELECT games.id FROM score WHERE user_id = ?) AND games.status = 'FINISHED' GROUP BY quizzes.id, games.date ORDER BY games.date DESC LIMIT ?";

    private static final String COUNT_NUMBER_OF_PLAYED_GAMES = "SELECT COUNT(*) FROM quizzes WHERE status='ACTIVE'";

    private static final String REMOVE_TAGS = "DELETE FROM quizzes_tags WHERE quiz_id = ?";


    private static final String UPDATE_QUIZ_STATUS = "UPDATE quizzes SET status=?::status_type WHERE id = ?";
    private static final String ADD_MODERATOR_COMMENT = "INSERT INTO rejected_message (quiz_id, moderator_id, comment, date)  VALUES(?,?,?,?)";

    private static final String ADD_MODERATOR_QUIZ = "INSERT INTO moderators_quizzes (moderator_id, quiz_id, assignment_date) VALUES (?,?,CURRENT_DATE)";
    private static final String GET_MODERATORS_QUIZZES = "SELECT quiz.date quizDate, quiz.authorName authorName, quiz.authorEmail authorEmail, quiz.authorSurname authorSurname, quiz.quizId id, quiz.quizName quizName, quiz.category category\n" +
            "FROM (SELECT users.name authorName, users.surname authorSurname, users.email authorEmail, q.id quizId, q.name quizName, q.date, category\n" +
            "      FROM (SELECT q.id, q.name, q.author, q.date, c.name category\n" +
            "            FROM quizzes q INNER JOIN categories c ON q.category_id = c.id) q\n" +
            "               INNER JOIN users ON users.id=q.author) quiz INNER JOIN moderators_quizzes on quiz_id=quiz.quizId where moderator_id = ?";
    private static final String GET_FILTERED_PENDING_QUIZZES = "SELECT quizzes.id id, quizzes.name quizName, date quizDate, categories.name AS category, users.name AS authorName, users.surname AS authorSurname, users.email AS authorEmail FROM quizzes INNER JOIN categories ON categories.id = category_id INNER JOIN users ON quizzes.author = users.id WHERE quizzes.status='PENDING' and (quizzes.name ~* ? OR categories.name ~* ? OR CONCAT(users.name, ' ', surname) ~*? OR date::text ~* ?)";

    private static final String GET_REJECTED_QUIZZES_CREATED_BY_USER_ID = "SELECT quizzes.id, quizzes.name, image, author, category_id, date, description, status, modification_time, categories.id, categories.name AS category " +
            "FROM quizzes INNER JOIN categories ON categories.id = category_id " +
            "WHERE author = ? AND status = 'DEACTIVATED'";
    private static final String GET_REJECTED_MESSAGES = "SELECT comment, date FROM rejected_message WHERE quiz_id = ? ORDER BY date desc";

    private static final String GET_COMMENTS = "SELECT r.comment commentText, r.date commentDate, r.quiz_id quizId,r.moderator_id moderatorId, u.email moderatorEmail, u.name moderatorName, u.surname moderatorSurname from rejected_message r inner join users u on r.moderator_id = u.id where r.quiz_id = ?";
    private static final String DELETE_ALL_MODERATOR_QUIZ = "DELETE FROM moderators_quizzes WHERE moderator_id = ?";
    private static final String DELETE_MODERATOR_QUIZ = "DELETE FROM moderators_quizzes WHERE quiz_id = ?";

    public List<QuizDto> getQuizzesByStatus(StatusType status) {

        List<QuizDto> quizDtos = jdbcTemplate.query(
                GET_QUIZZES_BY_STATUS_NAME,
                new Object[]{status.toString()}, (resultSet, i) -> {
                    QuizDto quiz = new QuizDto();
                    quiz.setName(resultSet.getString(QUIZ_NAME));
                    quiz.setCategoryId(resultSet.getInt(QUIZ_CATEGORY_ID));
                    quiz.setStatus(StatusType.valueOf(resultSet.getString(QUIZ_STATUS)));
                    quiz.setCategory(resultSet.getString(CATEGORY));
                    quiz.setId(resultSet.getInt(ID));
                    quiz.setAuthor(resultSet.getInt(AUTHOR_ID));
                    quiz.setAuthorName(resultSet.getString(AUTHOR_NAME));
                    quiz.setAuthorSurname(resultSet.getString(AUTHOR_SURNAME));
                    quiz.setAuthorEmail(resultSet.getString(AUTHOR_EMAIL));
                    quiz.setDate(resultSet.getDate(QUIZ_DATE));
                    quiz.setDescription(resultSet.getString(QUIZ_DESCRIPTION));
                    quiz.setModificationTime(resultSet.getTimestamp(QUIZ_MOD_TIME));

                    return quiz;
                });

        if (quizDtos.isEmpty()) {
            return Collections.emptyList();
        }

        return quizDtos;
    }

    public List<QuizDto> getAllQuizzes(int limit, int offset, int userId) {
        List<Quiz> quizzes = jdbcTemplate.query(GET_ALL_QUIZZES, new Object[]{limit, limit * offset}, new QuizMapper());

        if (quizzes.isEmpty()) {
            return Collections.emptyList();
        }

        List<QuizDto> quizDtoList = quizConverter.toQuizDtoList(quizzes);


        if (userId == 0) {
            quizDtoList.forEach(quiz -> quiz.setTags(getQuizTags(quiz.getId())));
        } else {
            quizDtoList.forEach(quiz -> quiz.setTags(getQuizTags(quiz.getId())));
            quizDtoList.forEach(quiz -> quiz.setFavorite(isQuizFavorite(quiz.getId(), userId)));
        }

        return quizDtoList;
    }

    public QuizDto findInfoById(int id) {
        List<QuizDto> quizzes;

        try {
            quizzes = jdbcTemplate.query(
                    GET_QUIZ_BY_ID_NAME,
                    new Object[]{id}, (resultSet, i) -> {
                        QuizDto quiz = new QuizDto();
                        quiz.setName(resultSet.getString(QUIZ_SHORT_NAME));
                        quiz.setCategoryId(resultSet.getInt(QUIZ_SHORT_CATEGORY_ID));
                        quiz.setStatus(StatusType.valueOf(resultSet.getString(QUIZ_SHORT_STATUS)));
                        quiz.setCategory(resultSet.getString(CATEGORY_SHORT_NAME));
                        quiz.setId(resultSet.getInt(QUIZ_SHORT_ID));
                        quiz.setAuthor(resultSet.getInt(QUIZ_SHORT_AUTHOR));
                        quiz.setAuthorName(resultSet.getString(USER_SHORT_NAME));
                        quiz.setAuthorSurname(resultSet.getString(USER_SHORT_SURNAME));
                        quiz.setAuthorEmail(resultSet.getString(USER_SHORT_EMAIL));
                        quiz.setDate(resultSet.getDate(QUIZ_SHORT_DATE));
                        quiz.setDescription(resultSet.getString(QUIZ_SHORT_DESCRIPTION));
                        quiz.setModificationTime(resultSet.getTimestamp(QUIZ_MODIFICATION_TIME));

                        return quiz;
                    }
            );
            if (quizzes.isEmpty()) {
                return null;
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new DatabaseException(String.format("Find quiz by id '%s' database error occurred", id));
        }

        return quizzes.get(0);
    }

    public Quiz findById(int id) {
        List<Quiz> quizzes;

        try {
            quizzes = jdbcTemplate.query(
                    GET_QUIZ_BY_ID,
                    new Object[]{id}, (resultSet, i) -> {
                        Quiz quiz = new Quiz();

                        quiz.setId(resultSet.getInt(ID));
                        quiz.setName(resultSet.getString(NAME));
                        quiz.setAuthor(resultSet.getInt(AUTHOR));
                        quiz.setCategoryId(resultSet.getInt(CATEGORY));
                        quiz.setDate(resultSet.getDate(DATE));
                        quiz.setDescription(resultSet.getString(DESCRIPTION));
                        quiz.setStatus(StatusType.valueOf(resultSet.getString(STATUS)));
                        quiz.setModificationTime(resultSet.getTimestamp(MODIFICATION_TIME));
                        return quiz;
                    }
            );
            if (quizzes.isEmpty()) {
                return null;
            }
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Find quiz by id '%s' database error occurred", id));
        }

        return quizzes.get(0);
    }

    public List<Quiz> getQuizzesCreatedByUser(int userId, String sort) {

        List<Quiz> quizzesCreatedByUser = jdbcTemplate.query(
                sort.isEmpty() ? GET_QUIZZES_CREATED_BY_USER_ID : GET_QUIZZES_CREATED_BY_USER_ID + "ORDER BY " + sort,
                new Object[]{userId},
                new QuizMapper());

        if (quizzesCreatedByUser.isEmpty()) {
            return Collections.emptyList();
        }

        return quizzesCreatedByUser;
    }

    public List<Quiz> findQuizzesByName(String name) {

        List<Quiz> quizzesByName = jdbcTemplate.query(GET_QUIZZES_BY_NAME, new Object[]{"%" + name + "%"}, new QuizMapper());

        if (quizzesByName.isEmpty()) {
            return Collections.emptyList();
        }

        return quizzesByName;
    }

    public List<QuizDto> getFavoriteQuizzesByUserId(int userId) {
        List<Quiz> quizzesFavoriteByUser = jdbcTemplate.query(GET_FAVORITE_QUIZZES_BY_USER_ID, new Object[]{userId}, new QuizMapper());

        if (quizzesFavoriteByUser.isEmpty()) {
            return Collections.emptyList();
        }

        List<QuizDto> quizDtoList = quizConverter.toQuizDtoList(quizzesFavoriteByUser);

        quizDtoList.forEach(quiz -> quiz.setTags(getQuizTags(quiz.getId())));
        quizDtoList.forEach(quiz -> quiz.setFavorite(isQuizFavorite(quiz.getId(), userId)));

        return quizDtoList;
    }

    public List<QuizDto> getQuizzesByCategory(int categoryId, int userId) {

        List<Quiz> quizzesByCategory = jdbcTemplate.query(GET_QUIZZES_BY_CATEGORY_ID, new Object[]{categoryId}, new QuizMapper());

        if (quizzesByCategory.isEmpty()) {
            return Collections.emptyList();
        }

        List<QuizDto> quizDtoList = quizConverter.toQuizDtoList(quizzesByCategory);

        if (userId == 0) {
            quizDtoList.forEach(quiz -> quiz.setTags(getQuizTags(quiz.getId())));
        } else {
            quizDtoList.forEach(quiz -> quiz.setTags(getQuizTags(quiz.getId())));
            quizDtoList.forEach(quiz -> quiz.setFavorite(isQuizFavorite(quiz.getId(), userId)));
        }


        return quizDtoList;
    }

    public List<Quiz> getQuizzesByTag(int tagId) {

        List<Quiz> quizzesByTag = jdbcTemplate.query(GET_QUIZZES_BY_TAG, new Object[]{tagId}, new QuizMapper());

        if (quizzesByTag.isEmpty()) {
            return Collections.emptyList();
        }

        return quizzesByTag;
    }

    public String getQuizImageByQuizId(int quizId) {
        return jdbcTemplate.queryForObject(
                GET_QUIZ_IMAGE_BY_QUIZ_ID,
                new Object[]{quizId},
                (resultSet, i) -> resultSet.getString(IMAGE));
    }

    @Transactional
    public QuizDto insert(QuizDto entity) {
        if (entity.getId() != null) {
            return update(entity);
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(INSERT_QUIZ, new String[]{ID});
                ps.setString(1, entity.getName());
                ps.setInt(2, entity.getAuthor());
                ps.setInt(3, entity.getCategoryId());
                ps.setString(4, entity.getDescription());
                ps.setString(5, String.valueOf(entity.getStatus()));
                ps.setTimestamp(6, entity.getModificationTime());
                ps.setString(7, entity.getImage());
                return ps;
            }, keyHolder);
        } catch (DataAccessException e) {
            throw new DatabaseException("Database access exception while quiz insert");
        }

        if (keyHolder.getKey() == null) return null;

        entity.setId(keyHolder.getKey().intValue());

        for (Tag tag : entity.getTags()) {
            addTagToQuiz(entity.getId(), tagDao.insert(tag).getId());
        }

        for (QuestionDto question : entity.getQuestions()) {
            questionDao.insert(question, keyHolder.getKey().intValue());
        }

        return entity;
    }

    private QuizDto update(QuizDto entity) {
        if (entity.isChanged()) {
            try {
                jdbcTemplate.update(UPDATE_QUIZ,
                        entity.getName(),
                        entity.getAuthor(),
                        entity.getCategoryId(),
                        entity.getDescription(),
                        String.valueOf(entity.getStatus()),
                        entity.getModificationTime(),
                        entity.getImage(),
                        entity.getId()
                );
            } catch (DataAccessException e) {
                throw new DatabaseException("Database access exception while quiz insert");
            }
        }

        removeTags(entity.getId());

        for (Tag tag : entity.getTags()) {
            addTagToQuiz(entity.getId(), tagDao.insert(tag).getId());
        }

        for (QuestionDto question : entity.getQuestions()) {
            if (question.getId() != null)
                questionDao.update(question, entity.getId());
        }

        return entity;
    }

    public QuizDto getQuizInfo(int quizId) {
        QuizDto quizDto = this.jdbcTemplate.queryForObject(GET_QUIZ_BY_ID,
                new Object[]{quizId},
                (resultSet, i) -> {
                    QuizDto quiz = new QuizDto();

                    quiz.setId(resultSet.getInt(QUIZ_ID));
                    quiz.setName(resultSet.getString(QUIZ_NAME));
                    quiz.setImage(resultSet.getString(QUIZ_IMAGE));
                    quiz.setAuthor(resultSet.getInt(QUIZ_AUTHOR));
                    quiz.setCategoryId(resultSet.getInt(QUIZ_CATEGORY_ID));
                    quiz.setDate(resultSet.getDate(QUIZ_DATE));
                    quiz.setDescription(resultSet.getString(QUIZ_DESCRIPTION));
                    quiz.setStatus(StatusType.valueOf(resultSet.getString(QUIZ_STATUS)));
                    quiz.setModificationTime(resultSet.getTimestamp(QUIZ_MODIFICATION_TIME));

                    return quiz;
                });

        quizDto.setTags(getQuizTags(quizDto.getId()));

        quizDto.setQuestions(questionDao.getQuestionInfoByQuizId(quizDto.getId()));

        for (QuestionDto question : quizDto.getQuestions()) {
            question.setAnswerList(answerDao.findAnswerInfoByQuestionId(question.getId()));
        }

        return quizDto;
    }

    public boolean addTagToQuiz(int quizId, int tagId) {
        int affectedRowNumber;
        try {
            affectedRowNumber = jdbcTemplate.update(ADD_TAG_TO_QUIZ, quizId, tagId);
        } catch (DataAccessException e) {
            throw new DatabaseException("Database access exception while quiz-tag insert");
        }
        return affectedRowNumber > 0;
    }

    public void removeTags(int quizId) {
        jdbcTemplate.update(REMOVE_TAGS, quizId);
    }

    public List<Quiz> getTopPopularQuizzes(int limit) {
        List<Quiz> quizzes = jdbcTemplate.query(
                GET_TOP_POPULAR_QUIZZES,
                new Object[]{limit}, (resultSet, i) -> {
                    Quiz quiz = new Quiz();

                    quiz.setId(resultSet.getInt(ID));
                    quiz.setName(resultSet.getString(NAME));
                    quiz.setAuthor(resultSet.getInt(AUTHOR));
                    quiz.setCategoryId(resultSet.getInt(CATEGORY_ID));
                    quiz.setDate(resultSet.getDate(DATE));
                    quiz.setDescription(resultSet.getString(DESCRIPTION));
                    quiz.setStatus(StatusType.valueOf(resultSet.getString(STATUS)));
                    quiz.setModificationTime(resultSet.getTimestamp(MODIFICATION_TIME));
                    return quiz;
                }
        );
        if (quizzes.isEmpty()) {
            return Collections.emptyList();
        }

        return quizzes;
    }

    public List<Quiz> getTopPopularQuizzesByCategory(int categoryId, int limit) {
        List<Quiz> quizzes = jdbcTemplate.query(
                GET_TOP_POPULAR_QUIZZES_BY_CATEGORY,
                new Object[]{categoryId, limit}, (resultSet, i) -> {
                    Quiz quiz = new Quiz();

                    quiz.setId(resultSet.getInt(ID));
                    quiz.setName(resultSet.getString(NAME));
                    quiz.setAuthor(resultSet.getInt(AUTHOR));
                    quiz.setDate(resultSet.getDate(DATE));
                    quiz.setDescription(resultSet.getString(DESCRIPTION));
                    quiz.setStatus(StatusType.valueOf(resultSet.getString(STATUS)));
                    quiz.setModificationTime(resultSet.getTimestamp(MODIFICATION_TIME));
                    return quiz;
                }
        );
        if (quizzes.isEmpty()) {
            return Collections.emptyList();
        }

        return quizzes;
    }

    public List<Quiz> getRecentGames(int userId, int limit) {
        List<Quiz> quizzes = jdbcTemplate.query(
                GET_RECENT_GAMES,
                new Object[]{userId, limit}, (resultSet, i) -> {
                    Quiz quiz = new Quiz();

                    quiz.setId(resultSet.getInt(ID));
                    quiz.setName(resultSet.getString(NAME));
                    quiz.setAuthor(resultSet.getInt(AUTHOR));
                    quiz.setCategoryId(resultSet.getInt(CATEGORY_ID));
                    quiz.setDate(resultSet.getDate(DATE));
                    quiz.setDescription(resultSet.getString(DESCRIPTION));
                    quiz.setStatus(StatusType.valueOf(resultSet.getString(STATUS)));
                    quiz.setModificationTime(resultSet.getTimestamp(MODIFICATION_TIME));
                    return quiz;
                }
        );
        if (quizzes.isEmpty()) {
            return Collections.emptyList();
        }

        return quizzes;
    }

    public List<Quiz> getRecommendations(int userId, int limit) {
        List<Quiz> quizzes = jdbcTemplate.query(
                GET_QUIZ_RECOMMENDATIONS,
                new Object[]{userId, limit}, new QuizMapper()
        );
        if (quizzes.isEmpty()) {
            return Collections.emptyList();
        }

        return quizzes;
    }

    public List<Quiz> getRecommendationsByFriends(int userId, int limit) {
        List<Quiz> quizzes = jdbcTemplate.query(
                GET_QUIZ_RECOMMENDATIONS_BY_FRIENDS,
                new Object[]{userId, limit}, (resultSet, i) -> {
                    Quiz quiz = new Quiz();

                    quiz.setId(resultSet.getInt(ID));
                    quiz.setName(resultSet.getString(NAME));
                    quiz.setAuthor(resultSet.getInt(AUTHOR));
                    quiz.setCategoryId(resultSet.getInt(CATEGORY));
                    quiz.setDate(resultSet.getDate(DATE));
                    quiz.setDescription(resultSet.getString(DESCRIPTION));
                    quiz.setStatus(StatusType.valueOf(resultSet.getString(STATUS)));
                    quiz.setModificationTime(resultSet.getTimestamp(MODIFICATION_TIME));
                    return quiz;
                }
        );
        if (quizzes.isEmpty()) {
            return Collections.emptyList();
        }

        return quizzes;
    }

    public List<QuizDto> getQuizzesByFilter(String searchByUser, int userId) {
        List<Quiz> getFilteredQuizzes = jdbcTemplate.query(
                GET_FILTERED_QUIZZES,
                new Object[]{searchByUser, searchByUser, searchByUser, searchByUser},
                new QuizMapper());

        if (getFilteredQuizzes.isEmpty()) {
            return Collections.emptyList();
        }

        List<QuizDto> quizDtoList = quizConverter.toQuizDtoList(getFilteredQuizzes);

        quizDtoList = quizDtoList.stream().distinct().collect(Collectors.toList());
        quizDtoList.forEach(quiz -> quiz.setTags(getQuizTags(quiz.getId())));
        quizDtoList.forEach(quiz -> quiz.setFavorite(isQuizFavorite(quiz.getId(), userId)));

        return quizDtoList;
    }

    public boolean markQuizAsFavorite(int quizId, int userId) {
        int affectedRowNumber = jdbcTemplate.update(MARK_QUIZ_AS_FAVORITE, userId, quizId);

        return affectedRowNumber > 0;
    }

    public boolean unmarkQuizAsFavorite(int quizId, int userId) {
        int affectedRowNumber = jdbcTemplate.update(UNMARK_QUIZ_AS_FAVORITE, userId, quizId);

        return affectedRowNumber > 0;
    }

    private List<Tag> getQuizTags(int quizId) {
        return jdbcTemplate.query(
                GET_TAGS_BY_QUIZ_ID,
                new Object[]{quizId}, (resultSet, i) -> {
                    Tag tag = new Tag();
                    tag.setName(resultSet.getString(NAME));
                    return tag;
                });
    }

    private boolean isQuizFavorite(int quizId, int userId) {
        List<Integer> answer = jdbcTemplate.query(
                IS_FAVORITE_QUIZ,
                new Object[]{quizId, userId},
                (resultSet, i) -> resultSet.getInt(QUIZ_ID)
        );

        return !answer.isEmpty();
    }

    public List<QuizDto> getPopularQuizzes(int limit) {
        List<Quiz> quizzes = jdbcTemplate.query(
                GET_POPULAR_QUIZ,
                new Object[]{limit}, new QuizMapper());

        if (quizzes.isEmpty()) {
            return Collections.emptyList();
        }

        List<QuizDto> quizDtoList = quizConverter.toQuizDtoList(quizzes);

        quizDtoList.forEach(quiz -> quiz.setTags(getQuizTags(quiz.getId())));

        return quizDtoList;
    }

    public List<Quiz> filterQuizzesByUserId(String userSearch, int userId, String sort) {
        List<Quiz> quizzes = jdbcTemplate.query(
                sort.isEmpty() ? FILTER_QUIZZES_CREATED_BY_USER : FILTER_QUIZZES_CREATED_BY_USER + "ORDER BY " + sort,
                new Object[]{userId, userSearch, userSearch, userSearch},
                new QuizMapper());

        if (quizzes.isEmpty()) {
            return Collections.emptyList();
        }
        return quizzes;
    }

    public boolean updateStatusById(int id, StatusType status) {
        int affectedNumberOfRows = jdbcTemplate.update(UPDATE_QUIZ_STATUS, status.toString(), id);
        return affectedNumberOfRows > 0;
    }

    public ModeratorComment addCommentByQuizId(ModeratorComment comment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(ADD_MODERATOR_COMMENT, new String[]{ID});
                ps.setInt(1, comment.getQuizId());
                ps.setInt(2, comment.getModeratorId());
                ps.setString(3, comment.getComment());
                ps.setDate(4, comment.getCommentDate());
                return ps;
            }, keyHolder);
        } catch (DataAccessException e) {
            throw new DatabaseException("Database access exception while comment insert");
        }
        comment.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return comment;
    }

    public List<QuizDto> searchInFavoriteQuizzes(int userId, String userSearch) {
        List<Quiz> quizzes = jdbcTemplate.query(
                FILTER_FAVORITE_QUIZZES,
                new Object[]{userId, userSearch, userSearch, userSearch, userSearch},
                new QuizMapper()
        );

        if (quizzes.isEmpty()) {
            return Collections.emptyList();
        }

        List<QuizDto> quizDtoList = quizConverter.toQuizDtoList(quizzes);

        quizDtoList.forEach(quiz -> quiz.setTags(getQuizTags(quiz.getId())));
        quizDtoList.forEach(quiz -> quiz.setFavorite(isQuizFavorite(quiz.getId(), userId)));

        return quizDtoList;
    }

    public int getNumberOfRecord() {
        return jdbcTemplate.queryForObject(COUNT_NUMBER_OF_PLAYED_GAMES,
                (resultSet, i) -> resultSet.getInt(COUNT));
    }

    public boolean assignModeratorById(int quizId, int moderatorId) {
        int affectedNumberOfRows = jdbcTemplate.update(ADD_MODERATOR_QUIZ, moderatorId, quizId);
        return affectedNumberOfRows > 0;
    }

    public List<QuizDto> getModeratorQuizzes(int moderatorId) {
        List<QuizDto> quizDtos = jdbcTemplate.query(
                GET_MODERATORS_QUIZZES,
                new Object[]{moderatorId}, (resultSet, i) -> {
                    QuizDto quiz = new QuizDto();
                    quiz.setName(resultSet.getString(QUIZ_NAME));
                    quiz.setId(resultSet.getInt(ID));
                    quiz.setDate(resultSet.getDate(QUIZ_DATE));
                    quiz.setAuthorName(resultSet.getString(AUTHOR_NAME));
                    quiz.setAuthorSurname(resultSet.getString(AUTHOR_SURNAME));
                    quiz.setAuthorEmail(resultSet.getString(AUTHOR_EMAIL));
                    quiz.setCategory(resultSet.getString(CATEGORY));

                    return quiz;
                });

        if (quizDtos.isEmpty()) {
            return Collections.emptyList();
        }

        return quizDtos;
    }

    public List<QuizDto> getPendingQuizzesByFilter(String searchText) {
        List<QuizDto> getFilteredQuizzes = jdbcTemplate.query(
                GET_FILTERED_PENDING_QUIZZES,
                new Object[]{searchText, searchText, searchText, searchText}, (resultSet, i) -> {
                    QuizDto quiz = new QuizDto();
                    quiz.setName(resultSet.getString(QUIZ_NAME));
                    quiz.setId(resultSet.getInt(ID));
                    quiz.setDate(resultSet.getDate(QUIZ_DATE));
                    quiz.setAuthorName(resultSet.getString(AUTHOR_NAME));
                    quiz.setAuthorSurname(resultSet.getString(AUTHOR_SURNAME));
                    quiz.setAuthorEmail(resultSet.getString(AUTHOR_EMAIL));
                    quiz.setCategory(resultSet.getString(CATEGORY));

                    return quiz;
                });
        if (getFilteredQuizzes.isEmpty()) {
            return Collections.emptyList();
        }
        getFilteredQuizzes = getFilteredQuizzes.stream().distinct().collect(Collectors.toList());

        return getFilteredQuizzes;
    }


    public List<ModeratorCommentDto> getCommentHistory(int quizId) {
        List<ModeratorCommentDto> comments = jdbcTemplate.query(
                GET_COMMENTS,
                new Object[]{quizId}, (resultSet, i) -> {
                    ModeratorCommentDto comment = new ModeratorCommentDto();
                    comment.setQuizId(resultSet.getInt(QUIZ_ID));
                    comment.setComment(resultSet.getString(COMMENT_TEXT));
                    comment.setCommentDate(resultSet.getDate(COMMENT_DATE));
                    comment.setModeratorId(resultSet.getInt(MODERATOR_ID));
                    comment.setModeratorName(resultSet.getString(MODERATOR_NAME));
                    comment.setModeratorSurname(resultSet.getString(MODERATOR_SURNAME));
                    comment.setModeratorEmail(resultSet.getString(MODERATOR_EMAIL));
                    return comment;
                });
        if (comments.isEmpty()) {
            return Collections.emptyList();
        }
        return comments;
    }

    public void unassignQuizById(int id) {
        jdbcTemplate.update(DELETE_MODERATOR_QUIZ, id);
    }

    public List<Quiz> getRejectedQuizzesByUserId(int userId, String sort) {
        List<Quiz> quizzesCreatedByUser = jdbcTemplate.query(
                sort.isEmpty() ? GET_REJECTED_QUIZZES_CREATED_BY_USER_ID : GET_REJECTED_QUIZZES_CREATED_BY_USER_ID + "ORDER BY " + sort,
                new Object[]{userId},
                new QuizMapper());

        if (quizzesCreatedByUser.isEmpty()) {
            return Collections.emptyList();
        }

        return quizzesCreatedByUser;
    }

    public List<RejectMessage> getRejectMessages(int quizId) {
        return jdbcTemplate.query(GET_REJECTED_MESSAGES,
                new Object[]{quizId},
                ((resultSet, i) -> new RejectMessage(resultSet.getString(COMMENT), resultSet.getDate(DATE))));
    }

    public void unassignAllQuizById(int moderatorId) {
        jdbcTemplate.update(DELETE_ALL_MODERATOR_QUIZ, moderatorId);
    }
}
