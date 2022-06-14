package com.quiz.service;

import com.quiz.data.dao.UserDao;
import com.quiz.data.entities.NotificationStatus;
import com.quiz.data.entities.User;
import com.quiz.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserDao userDao;

    private static final String ALL_STATUS = "AllStatus";
    private static final String ALL_ROLE = "AllRole";

    public User findByEmail(String email) {
        User userDB = userDao.findByEmail(email);
        if (userDB == null) {
            throw new UserNotFoundException("email", email);
        }
        return userDB;
    }

    public User findById(int id) {
        return userDao.findById(id);
    }

    public User findProfileInfoByUserId(int id) {
        return userDao.findProfileInfoByUserId(id);
    }

    public boolean updateUser(User user) {
        return userDao.updateUser(user);
    }

    public boolean updatePasswordById(int id, String newPassword) {
        return userDao.updatePasswordById(id, passwordEncoder.encode(newPassword));
    }

    public boolean updateStatusById(int id) {
        return userDao.updateStatusById(id);
    }

    public int getUserIdByEmail(String email) {
        return userDao.getUserIdByEmail(email);
    }

    public String getUserRoleByEmail(String email) {
        return userDao.getUserRoleByEmail(email);
    }

    public boolean updateProfileImage(String imageUrl, int userId) {
        return userDao.updateProfileImage(imageUrl, userId);
    }

    public String getImageByUserId(int userId) {
        return userDao.getUserImageByUserId(userId);
    }

    public boolean changeNotificationStatus(String status, int userId) {
        return userDao.updateNotificationStatus(status, userId);
    }

    public List<User> findUsersByRoleStatus(String role, String status) {
        if (status.equals(ALL_STATUS) && role.equals(ALL_ROLE)) {
            return userDao.findAdminsUsers();
        }
        if (status.equals(ALL_STATUS)) {
            return userDao.getUsersByRole(role);
        }
        if (role.equals(ALL_ROLE)) {
            return userDao.getUsersByStatus(status);
        }

        return userDao.getUsersByRoleStatus(role, status);
    }

    public List<User> getUsersByFilter(String searchByUser) {
        return userDao.getUsersByFilter(searchByUser);
    }

    public void deleteUserById(int id) {
        userDao.deleteUserById(id);
    }

    public NotificationStatus getNotificationStatus(int userId) {
        return userDao.getUserNotification(userId);
    }

    public Integer getRatingByUser(int userId) {
        return userDao.getRatingByUser(userId);
    }

    public List<User> getRating(int from, int to) {
        return userDao.getRating(from, to);
    }

    public List<User> getRatingInRange(int userId, int range) {
        return userDao.getRatingInRange(userId, range);
    }

    public boolean activateUser(String code) {
        User user = userDao.findByActivationCode(code);
        if (user == null) {
            return false;
        }
        user.setPassword(null);
        userDao.insert(user);
        return true;
    }

    public User findByActivationCode(String code) {
        return userDao.findByActivationCode(code);
    }
}

