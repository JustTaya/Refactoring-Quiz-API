package com.quiz.data.dao;

import com.quiz.data.dao.mapper.TagMapper;
import com.quiz.data.entities.Tag;
import com.quiz.exceptions.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;

import static com.quiz.data.dao.mapper.TagMapper.ID;

@Repository
@RequiredArgsConstructor
public class TagDao {
    private final JdbcTemplate jdbcTemplate;

    private static final String RESOURCE = "tag";

    private static final String TAG_BY_ID = "SELECT id, name FROM tags WHERE id = ?";
    private static final String TAG_BY_NAME = "SELECT id, name FROM tags WHERE name = ?";
    private static final String TAGS_BY_NAME = "SELECT id, name FROM tags WHERE name SIMILAR TO ?";
    private static final String TAGS_BY_QUIZ = "SELECT id, name FROM tags INNER JOIN quizzes_tags on id = tag_id where quiz_id = ?";
    private static final String INSERT_TAG = "INSERT INTO tags (name) SELECT (?) WHERE NOT EXISTS(SELECT FROM tags WHERE name = ?)";

    public Tag getTagById(int id) {
        List<Tag> tags;

        try {
            tags = jdbcTemplate.query(
                    TAG_BY_ID,
                    new Object[]{id},
                    new TagMapper()
            );
        } catch (DataAccessException e) {
            throw DatabaseException.resourceSearchException(RESOURCE, "'tagId': " + id);
        }

        return tags.get(0);
    }

    public Tag getTagByName(String name) {
        List<Tag> tags;

        try {
            tags = jdbcTemplate.query(
                    TAG_BY_NAME,
                    new Object[]{name},
                    new TagMapper()
            );
        } catch (DataAccessException e) {
            throw DatabaseException.resourceSearchException(RESOURCE, "'name': " + name);
        }

        return tags.get(0);
    }

    public List<Tag> getTagsByName(String name) {
        List<Tag> tags;

        try {
            tags = jdbcTemplate.query(
                    TAGS_BY_NAME,
                    new Object[]{name},
                    new TagMapper()
            );
        } catch (DataAccessException e) {
            throw DatabaseException.resourceSearchException(RESOURCE, "'name': " + name);
        }

        return tags;
    }

    public List<Tag> getTagsByQuiz(int quizId) {
        List<Tag> tagsByQuiz = jdbcTemplate.query(TAGS_BY_QUIZ, new Object[]{quizId}, new TagMapper());
        if (tagsByQuiz.isEmpty()) {
            return Collections.emptyList();
        }
        return tagsByQuiz;
    }

    public Tag insert(Tag entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(INSERT_TAG, new String[]{ID});
                ps.setString(1, entity.getName());
                ps.setString(2, entity.getName());
                return ps;
            }, keyHolder);
        } catch (DataAccessException e) {
            throw DatabaseException.accessExceptionOnInsert(RESOURCE);
        }

        Tag tag = new Tag();
        if (keyHolder.getKey() == null) {
            tag.setId(getTagByName(entity.getName()).getId());
        } else {
            tag.setId(keyHolder.getKey().intValue());
        }

        return tag;
    }
}
