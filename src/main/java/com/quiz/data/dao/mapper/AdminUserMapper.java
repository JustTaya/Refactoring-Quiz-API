package com.quiz.data.dao.mapper;

import com.quiz.data.entities.Role;
import com.quiz.data.entities.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminUserMapper implements RowMapper<User> {

    public static final String ID = "id";
    public static final String EMAIL = "email";
    public static final String ROLE = "role";
    public static final String NAME = "name";
    public static final String SURNAME = "surname";
    public static final String ACTIVE = "active";

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();

        user.setId(resultSet.getInt(ID));
        user.setEmail(resultSet.getString(EMAIL));
        user.setRole(Role.valueOf(resultSet.getString(ROLE).trim()));
        user.setName(resultSet.getString(NAME));
        user.setSurname(resultSet.getString(SURNAME));
        user.setActive(resultSet.getBoolean(ACTIVE));

        return user;
    }
}
