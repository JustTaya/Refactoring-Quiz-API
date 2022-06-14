package com.quiz.service;

import com.quiz.data.dao.AnswerDao;
import com.quiz.data.dao.QuestionDao;
import com.quiz.data.dao.QuizDao;
import com.quiz.data.dto.QuestionCheckDto;
import com.quiz.data.dto.QuizCheckDto;
import com.quiz.data.dto.QuizDto;
import com.quiz.data.dto.ModeratorCommentDto;
import com.quiz.data.entities.Answer;
import com.quiz.data.entities.ModeratorComment;
import com.quiz.data.entities.Question;
import com.quiz.data.entities.StatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizCheckService {

    private final QuizDao quizDao;
    private final QuestionDao questionDao;
    private final AnswerDao answerDao;

    public QuizCheckDto getQuizCheck(int id) {
        List<Question> questions = questionDao.findQuestionsByQuizId(id);

        List<QuestionCheckDto> questionCheckDto = new ArrayList<>();
        for (Question question : questions) {
            List<Answer> answers = answerDao.findAnswersByQuestionId(question.getId());
            QuestionCheckDto questionDto = new QuestionCheckDto(question, answers);
            questionCheckDto.add(questionDto);
        }
        QuizDto quizDto = quizDao.findInfoById(id);
        return new QuizCheckDto(quizDto, questionCheckDto);
    }

    public boolean updateStatusById(int id, StatusType status) {
        return quizDao.updateStatusById(id, status);
    }

    public ModeratorComment addCommentByQuizId(ModeratorComment comment) {
        return quizDao.addCommentByQuizId(comment);
    }

    public boolean assignModerator(int quizId, int moderatorId) {
        return quizDao.assignModeratorById(quizId, moderatorId);
    }

    public List<ModeratorCommentDto> getCommentHistory(int quizId) {
        return quizDao.getCommentHistory(quizId);
    }
}
