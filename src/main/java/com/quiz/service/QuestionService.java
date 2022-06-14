package com.quiz.service;

import com.quiz.data.dao.AnswerDao;
import com.quiz.data.dao.QuestionDao;
import com.quiz.data.entities.Answer;
import com.quiz.data.entities.Question;
import com.quiz.data.entities.QuestionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionDao questionDao;
    private final AnswerDao answerDao;

    public Question findById(int id) {
        return questionDao.findById(id);
    }

    public List<Question> findQuestionsByQuizId(int id) {
        return questionDao.findQuestionsByQuizId(id);
    }

    public byte[] getQuestionByQuestionId(int questionId) {
        return questionDao.getQuestionImageByQuestionId(questionId);
    }

    public List<Question> getQuestionsByQuizId(int quizId) {
        List<Question> questions = questionDao.getQuestionsByQuizId(quizId);

        questions.stream()
                .filter(question -> question.getType() != QuestionType.ANSWER)
                .forEach(question -> {
                    List<Answer> answerList = answerDao.findAnswersByQuestionId(question.getId());
                    Collections.shuffle(answerList);
                    question.setAnswerList(answerList);
                });
        return questions;
    }
}
