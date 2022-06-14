package com.quiz.data.dao.mapper;

import com.quiz.data.entities.Tag;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class TagMapper implements RowMapper<Tag> {
    public static final String ID = "id";
    public static final String NAME = "name";


    @Override
    public Tag mapRow(ResultSet resultSet, int i) throws SQLException {
        Tag tag = new Tag();

        tag.setId(resultSet.getInt(ID));
        tag.setName(resultSet.getString(NAME));

        return tag;
    }
}
