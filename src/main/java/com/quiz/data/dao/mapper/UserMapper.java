package com.quiz.data.dao.mapper;

import com.quiz.data.entities.Gender;
import com.quiz.data.entities.NotificationStatus;
import com.quiz.data.entities.Role;
import com.quiz.data.entities.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserMapper implements RowMapper<User> {

    public static final String ID = "id";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    public static final String ROLE = "role";
    public static final String NAME = "name";
    public static final String SURNAME = "surname";
    public static final String BIRTHDATE = "birthdate";
    public static final String GENDER = "gender";
    public static final String COUNTRY_ID = "country_id";
    public static final String CITY = "city";
    public static final String RATING = "rating";
    public static final String ABOUT = "about";
    public static final String ACTIVE = "active";
    public static final String NOTIFICATIONS = "notifications";


    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();

        user.setId(resultSet.getInt(ID));
        user.setEmail(resultSet.getString(EMAIL));
        user.setPassword(resultSet.getString(PASSWORD));
        user.setRole(Role.valueOf(resultSet.getString(ROLE).trim()));
        user.setName(resultSet.getString(NAME));
        user.setSurname(resultSet.getString(SURNAME));
        user.setBirthdate(resultSet.getDate(BIRTHDATE));
        user.setGender(Gender.valueOf(resultSet.getString(GENDER)));
        user.setCountryId(resultSet.getInt(COUNTRY_ID));
        user.setCity(resultSet.getString(CITY));
        user.setRating(resultSet.getInt(RATING));
        user.setAbout(resultSet.getString(ABOUT));
        user.setActive(resultSet.getBoolean(ACTIVE));
        user.setNotification(NotificationStatus.valueOf(resultSet.getString(NOTIFICATIONS)));

        return user;
    }
}
