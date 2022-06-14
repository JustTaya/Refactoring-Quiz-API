package com.quiz.service;

import com.quiz.data.dao.UserDao;
import com.quiz.data.dto.UserDto;
import com.quiz.data.entities.Role;
import com.quiz.data.entities.User;
import com.quiz.exceptions.UserEmailExistException;
import com.quiz.exceptions.UserNotFoundException;
import com.quiz.exceptions.PasswordException;
import com.quiz.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserDao userDao;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto register(User user) {
        User foundedUser =userDao.findByEmail(user.getEmail());
        if(foundedUser != null){
            throw new UserEmailExistException("email", user.getEmail());
        }
        user.setPassword(user.getPassword());
        user.setRole((Role.USER));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.insert(user);
        return new UserDto(user);
    }

    public String login(User user) {
        User foundedUser = userDao.findByEmail(user.getEmail());
        if (foundedUser == null) {
            throw new UserNotFoundException("email", user.getEmail());
        }
        if(!passwordEncoder.matches(user.getPassword(), foundedUser.getPassword())){
            throw new PasswordException();
        }
        return tokenProvider.createToken(foundedUser.getEmail());
    }
}
