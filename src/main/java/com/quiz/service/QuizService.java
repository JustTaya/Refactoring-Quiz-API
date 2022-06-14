package com.quiz.service;

import com.quiz.data.dao.QuizDao;
import com.quiz.data.dto.QuizDto;
import com.quiz.data.entities.Quiz;
import com.quiz.data.entities.RejectMessage;
import com.quiz.data.entities.StatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizDao quizDao;

    public List<QuizDto> findQuizzesByStatus(StatusType status) {
        return quizDao.getQuizzesByStatus(status);
    }
    public List<QuizDto> getModeratorQuizzes(int moderatorId) {
        return quizDao.getModeratorQuizzes(moderatorId);
    }

    public List<QuizDto> findAllQuizzes(int limit, int offset, int userId) {
        return quizDao.getAllQuizzes(limit, offset, userId);
    }

    public Quiz findQuizById(int id) {
        return quizDao.findById(id);
    }

    public QuizDto getQuizInfo(int id){
        return quizDao.getQuizInfo(id);
    }

    public List<Quiz> findQuizzesCreatedByUserId(int userId, String sort) {
        return quizDao.getQuizzesCreatedByUser(userId, sort);
    }

    public List<QuizDto> findFavoriteQuizzes(int userId) {
        return quizDao.getFavoriteQuizzesByUserId(userId);
    }

    public List<QuizDto> findQuizzesByCategory(int categoryId, int userId) {
        return quizDao.getQuizzesByCategory(categoryId, userId);
    }

    public List<Quiz> findQuizzesByTag(int tagId) {
        return quizDao.getQuizzesByTag(tagId);
    }

    public List<Quiz> findQuizzesByName(String name) {
        return quizDao.findQuizzesByName(name);
    }

    public String getImageByQuizId(int quizId) {
        return quizDao.getQuizImageByQuizId(quizId);
    }

    public QuizDto insertQuiz(QuizDto quiz) {
        return quizDao.insert(quiz);
    }

    public List<Quiz> findTopPopularQuizzes(int limit) {
        return quizDao.getTopPopularQuizzes(limit);
    }

    public List<Quiz> findTopPopularQuizzesByCategory(int categoryId, int limit) {
        return quizDao.getTopPopularQuizzesByCategory(categoryId, limit);
    }

    public List<Quiz> findRecentGames(int userId, int limit) {
        return quizDao.getRecentGames(userId, limit);
    }

    public List<QuizDto> getQuizzesByFilter(String searchByUser, int userId) {
        return quizDao.getQuizzesByFilter(searchByUser, userId);
    }

    public boolean markQuizAsFavorite(int quizId, int userId) {
        return quizDao.markQuizAsFavorite(quizId, userId);
    }

    public boolean unmarkQuizAsFavorite(int quizId, int userId) {
        return quizDao.unmarkQuizAsFavorite(quizId, userId);
    }

    public List<Quiz> findRecommendations(int userId, int limit) {
        return quizDao.getRecommendations(userId, limit);
    }

    public List<Quiz> findRecommendationsByFriends(int userId, int limit) {
        return quizDao.getRecommendationsByFriends(userId, limit);
    }

    public List<QuizDto> findPopularQuizzes(int limit) {
        return quizDao.getPopularQuizzes(limit);
    }

    public List<Quiz> filterQuizzesByUserId(String userSearch, int userId, String sort) {
        return quizDao.filterQuizzesByUserId(userSearch, userId, sort);
    }

    public List<QuizDto> searchInFavoriteQuizzes(int userId, String userSearch) {
        return quizDao.searchInFavoriteQuizzes(userId, userSearch);
    }

    public int getNumberOfRecord() {
        return quizDao.getNumberOfRecord();
    }
    public List<QuizDto> getPendingQuizByFilter(String searchText) {
        return quizDao.getPendingQuizzesByFilter(searchText);
    }

    public void unsignQuizById(int id) { quizDao.unassignQuizById(id); }

    public List<Quiz> getRejectedQuizzesByUserId(int userId, String sort) {
        return quizDao.getRejectedQuizzesByUserId(userId, sort);
    }

    public List<RejectMessage> getRejectMessages(int quizId) {
        return quizDao.getRejectMessages(quizId);
    }

    public void unsignAllModeratorQuizById(int moderatorId) {
        quizDao.unassignAllQuizById(moderatorId);
    }
}
