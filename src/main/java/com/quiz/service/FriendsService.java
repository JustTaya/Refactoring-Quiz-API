package com.quiz.service;

import com.quiz.dao.UserDao;
import com.quiz.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendsService {
    private final UserDao userDao;

    public List<User> findFriendByUserId(int id, String sort) {
        return userDao.findFriendByUserId(id, sort);
    }

    public List<User> filterFriendByUserId(String userSearch, int userId, String sort) {
        return userDao.filterFriendByUserId(userSearch, userId, sort);
    }
}
