package com.quiz.service;

import com.quiz.dao.AnnouncementDao;
import com.quiz.dao.AnswerDao;
import com.quiz.dao.UserDao;
import com.quiz.dao.GameDao;
import com.quiz.dto.GameAnswersDto;
import com.quiz.dto.GameQuestionsDto;
import com.quiz.dto.GameSessionDto;
import com.quiz.entities.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GameService {
    private final ConcurrentHashMap<Integer, GameSession> currentGames = new ConcurrentHashMap<>();
    @Autowired
    QuestionService questionService;
    @Autowired
    GameDao gameDao;
    @Autowired
    UserDao userDao;
    @Autowired
    AnswerDao answerDao;
    @Autowired
    AnnouncementDao announcementDao;

    public int addGameSession(int quizId, int hostId, int questionTimer, int maxUsersNumber) {
        User host = userDao.findById(hostId);

        GameSession gameSession = new GameSession(hostId, questionsToMap(questionService.getQuestionsByQuizId(quizId)), questionTimer);

        gameSession.getPlayerSet().add(new Player(host.getId(), host.getName() + " " + host.getSurname(), true));

        int gameId = createGame(quizId, hostId, questionTimer, maxUsersNumber);
        this.currentGames.put(gameId, gameSession);
        return gameId;
    }

    private int createGame(int quizId, int hostId, int questionTimer, int maxUsersNumber) {
        return gameDao.insertGame(quizId, hostId, questionTimer, maxUsersNumber);
    }

    private Map<Integer, Question> questionsToMap(List<Question> questions) {
        return Collections.synchronizedMap(questions.stream().collect(Collectors.toMap(Question::getId, Function.identity(), (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                },
                LinkedHashMap::new)));
    }


    public GameSessionDto addUserInSession(int gameId, Player player) {

        if (this.currentGames.get(gameId).getPlayerSet().size() == this.gameDao.getUserNumberByGameId(gameId)) {
            throw new RuntimeException("The session is already full");
        }

        if (player.isAuthorize()) {
            User user = userDao.findById(player.getUserId());
            this.currentGames.get(gameId).addPlayer(new Player(user.getId(), user.getName() + " " + user.getSurname(), player.isAuthorize()));
        } else {
            this.currentGames.get(gameId).addPlayer(player);
        }

        GameSessionDto result = this.gameDao.getGame(gameId);
        result.setPlayers(new ArrayList<>(this.currentGames.get(gameId).getPlayerSet()));

        return result;
    }

    public GameSessionDto deleteGameSession(int gameId) {
        GameSession finishSession = this.currentGames.get(gameId);

        if (finishSession != null) {
            Set<Player> players = finishSession.getPlayerSet();

            players.stream().filter(Player::isAuthorize)
                    .forEach(user -> userDao.insertUserScore(user.getUserId(), gameId, user.getUserScore()));


            this.currentGames.remove(gameId);

            GameSessionDto result = this.gameDao.getGame(gameId);

            result.setPlayers(players.stream()
                    .sorted(Comparator.comparingInt(Player::getUserScore).reversed())
                    .collect(Collectors.toList()));

            result.getPlayers().stream().filter(Player::isAuthorize)
                    .forEach(player -> announcementDao.generateGameResultAnnouncement(player, result.getPlayers().indexOf(player)));
            return result;
        }
        return null;
    }

    public GameQuestionsDto nextQuestion(int gameId) {
        return this.currentGames.get(gameId).nextQuestion();
    }

    public boolean handleAnswer(int gameId, Player player, GameAnswersDto answer) {
        if (answer.getAnswers() != null && !answer.getAnswers().isEmpty()) {
            QuestionType questionType = this.currentGames.get(gameId).getQuestions().get(answer.getAnswers().get(0).getQuestionId()).getType();

            switch (questionType) {
                case ANSWER:
                    if (isRightAnswer(answer.getAnswers().get(0).getText(), answer.getAnswers().get(0).getQuestionId(), gameId)) {
                        this.currentGames.get(gameId).addScorePoint(4, player.getUserId(), player.isAuthorize());
                    }
                    break;
                case OPTION:
                    if (isRightOption(answer.getAnswers())) {
                        this.currentGames.get(gameId).addScorePoint(2, player.getUserId(), player.isAuthorize());
                    }

                    break;
                case BOOLEAN:
                    if (isRightBoolean(answer.getAnswers().get(0))) {
                        this.currentGames.get(gameId).addScorePoint(1, player.getUserId(), player.isAuthorize());
                    }
                    break;
                case SEQUENCE:
                    if (isRightSequence(answer.getAnswers())) {
                        this.currentGames.get(gameId).addScorePoint(3, player.getUserId(), player.isAuthorize());
                    }
                    break;
            }
        }
        return this.currentGames.get(gameId).isAllAnswerCollected();
    }

    private boolean isRightAnswer(String text, int questionNumber, int gameId) {
        return this.answerDao.findById(this.currentGames.get(gameId).getQuestions().get(questionNumber).getId()).getText().equalsIgnoreCase(text);
    }

    private boolean isRightOption(List<Answer> answers) {
        return answers.stream()
                .allMatch(answer -> this.answerDao.findById(answer.getId()).isCorrect());
    }

    private boolean isRightBoolean(Answer answer) {
        return this.answerDao.findAnswersByQuestionId(answer.getQuestionId()).get(0).getText().equals(answer.getText());
    }

    private boolean isRightSequence(List<Answer> answers) {
        for (int i = 0; i < answers.size() - 1; i++) {
            if (this.answerDao.findById(answers.get(i).getId()).getNextAnswerId() != this.answerDao.findById(answers.get(i + 1).getId()).getNextAnswerId()) {
                return false;
            }
        }
        return true;
    }

    public void onUserDisconnection(int userId, int gameId) {
        this.currentGames.get(gameId).getPlayerSet()
                .stream()
                .filter(player -> player.getUserId() == userId)
                .findFirst()
                .ifPresent(player -> saveDisconnectedPlayerResult(userId, gameId, player));
    }

    private void saveDisconnectedPlayerResult(int userId, int gameId, Player player) {
        if (player.isAuthorize()) {
            this.gameDao.saveScore(userId, gameId, player.getUserScore());
        }
        this.currentGames.get(gameId).getPlayerSet().remove(player);
    }
}
