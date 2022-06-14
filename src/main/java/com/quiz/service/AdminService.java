package com.quiz.service;

import com.quiz.data.dao.UserDao;
import com.quiz.data.dto.UserDto;
import com.quiz.data.entities.User;
import com.quiz.exceptions.UserEmailExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {
    @Value("client.app")
    private String urlPath;

    private final MailSenderService mailSenderService;

    private final UserDao userDao;

    public UserDto addAdminUser(User user) {
        User userDB = userDao.findByEmail(user.getEmail());
        if (userDB != null) {
            throw new UserEmailExistException("email", user.getEmail());
        }
        user.setRole(user.getRole());
        user.setPassword(UUID.randomUUID().toString());
        userDao.insert(user);

        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Dear,%s%n Welcome to Quizer. Visit: %s to activate/%s",
                    user.getEmail(),
                    urlPath,
                    user.getPassword()
            );
            mailSenderService.send(user.getEmail(), "Activation code", message);
        }
        return new UserDto(user);
    }

    public List<User> findAdminsUsers() {
        return userDao.findAdminsUsers();
    }
}
