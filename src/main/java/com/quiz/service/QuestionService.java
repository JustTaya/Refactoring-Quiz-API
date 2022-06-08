package com.quiz.service;

import com.quiz.dao.AnswerDao;
import com.quiz.dao.QuestionDao;
import com.quiz.entities.Answer;
import com.quiz.entities.Question;
import com.quiz.entities.QuestionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
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
