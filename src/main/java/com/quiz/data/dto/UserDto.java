package com.quiz.data.dto;

import com.quiz.data.entities.Gender;
import com.quiz.data.entities.NotificationStatus;
import com.quiz.data.entities.Role;
import com.quiz.data.entities.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class UserDto {

    private int id;
    private String email;
    private Role role;
    private String name;
    private String surname;
    private Date birthdate;
    private Gender gender;
    private int countryId;
    private String city;
    private int rating;
    private String about;
    private boolean active;
    private NotificationStatus notification;
    private int languageId;

    public UserDto(User user) {
        id = user.getId();
        email = user.getEmail();
        role = user.getRole();
    }

}
