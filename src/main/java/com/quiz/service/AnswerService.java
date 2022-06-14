package com.quiz.service;

import com.quiz.data.dao.AnswerDao;
import com.quiz.data.entities.Answer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerDao answerDao;

    public Answer findById(int id) {
        return answerDao.findById(id);
    }

    public List<Answer> findAnswersByQuestionId(int id) {
        return answerDao.findAnswersByQuestionId(id);
    }

    public byte[] getImageByAnswerId(int answerId) {
        return answerDao.getAnswerImageByAnswerId(answerId);
    }

    public boolean updateAnswerImage(MultipartFile image, int answerId) {
        return answerDao.updateAnswerImage(image, answerId);
    }
}
