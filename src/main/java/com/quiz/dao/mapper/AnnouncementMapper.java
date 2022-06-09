package com.quiz.dao.mapper;

import com.quiz.entities.Announcement;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AnnouncementMapper implements RowMapper<Announcement> {

    private static final String ANNOUNCEMENT_DATE = "date";
    private static final String ANNOUNCEMENT_TEXT = "text";
    private static final String ANNOUNCEMENT_GENERATED = "generated";
    @Override
    public Announcement mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Announcement(resultSet.getDate(ANNOUNCEMENT_DATE), resultSet.getString(ANNOUNCEMENT_TEXT), resultSet.getBoolean(ANNOUNCEMENT_GENERATED));
    }
}
