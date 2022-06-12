package com.quiz.service;

import com.quiz.dao.AnnouncementDao;
import com.quiz.dao.AnswerDao;
import com.quiz.dao.UserDao;
import com.quiz.dao.GameDao;
import com.quiz.dto.GameAnswersDto;
import com.quiz.dto.GameQuestionsDto;
import com.quiz.dto.GameSessionDto;
import com.quiz.entities.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {
    private final ConcurrentHashMap<Integer, GameSession> currentGames = new ConcurrentHashMap<>();
    private final QuestionService questionService;
    private final GameDao gameDao;
    private final UserDao userDao;
    private final AnswerDao answerDao;
    private final AnnouncementDao announcementDao;

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
        GameSession currentGameSession = this.currentGames.get(gameId);

        if (currentGameSession.getPlayerSet().size() == this.gameDao.getUserNumberByGameId(gameId)) {
            throw new RuntimeException("The session is already full");
        }

        if (player.isAuthorize()) {
            User user = userDao.findById(player.getUserId());
            currentGameSession.addPlayer(new Player(user.getId(), user.getName() + " " + user.getSurname(), player.isAuthorize()));
        } else {
            currentGameSession.addPlayer(player);
        }

        GameSessionDto result = this.gameDao.getGame(gameId);
        result.setPlayers(new ArrayList<>(currentGameSession.getPlayerSet()));

        return result;
    }

    public GameSessionDto deleteGameSession(int gameId) {
        GameSession finishSession = this.currentGames.get(gameId);

        if (finishSession != null) {
            Set<Player> players = finishSession.getPlayerSet();

            players.stream()
                    .filter(Player::isAuthorize)
                    .forEach(user -> userDao.insertUserScore(user.getUserId(), gameId, user.getUserScore()));


            this.currentGames.remove(gameId);

            GameSessionDto result = this.gameDao.getGame(gameId);

            result.setPlayers(players.stream()
                    .sorted(Comparator.comparingInt(Player::getUserScore).reversed())
                    .collect(Collectors.toList()));

            result.getPlayers().stream()
                    .filter(Player::isAuthorize)
                    .forEach(player -> announcementDao.generateGameResultAnnouncement(player, result.getPlayers().indexOf(player)));
            return result;
        }
        return null;
    }

    public GameQuestionsDto nextQuestion(int gameId) {
        return this.currentGames.get(gameId).nextQuestion();
    }

    public boolean handleAnswer(int gameId, Player player, GameAnswersDto gameAnswersDto) {
        List<Answer> answers = gameAnswersDto.getAnswers();
        GameSession currentGame = this.currentGames.get(gameId);

        if (!CollectionUtils.isEmpty(answers)) {
            Map<Integer, Question> questions = currentGame.getQuestions();
            Answer answer = gameAnswersDto.getAnswers().get(0);
            Question question = questions.get(answer.getQuestionId());

            switch (question.getType()) {
                case ANSWER:
                    if (isRightAnswer(questions, answer.getText(), answer.getQuestionId())) {
                        currentGame.addScorePoint(4, player.getUserId(), player.isAuthorize());
                    }
                    break;
                case OPTION:
                    if (isRightOption(answers)) {
                        currentGame.addScorePoint(2, player.getUserId(), player.isAuthorize());
                    }
                    break;
                case BOOLEAN:
                    if (isRightBoolean(answer)) {
                        currentGame.addScorePoint(1, player.getUserId(), player.isAuthorize());
                    }
                    break;
                case SEQUENCE:
                    if (isRightSequence(answers)) {
                        currentGame.addScorePoint(3, player.getUserId(), player.isAuthorize());
                    }
                    break;
            }
        }
        return currentGame.isAllAnswerCollected();
    }

    private boolean isRightAnswer(Map<Integer, Question> questions, String text, int questionNumber) {
        Question question = questions.get(questionNumber);
        String correctAnswerText = this.answerDao.findById(question.getId()).getText();

        return correctAnswerText.equalsIgnoreCase(text);
    }

    private boolean isRightOption(List<Answer> answers) {
        return answers.stream()
                .allMatch(answer -> this.answerDao.findById(answer.getId()).isCorrect());
    }

    private boolean isRightBoolean(Answer answer) {
        String correctAnswerText = this.answerDao.findAnswersByQuestionId(answer.getQuestionId()).get(0).getText();

        return correctAnswerText.equals(answer.getText());
    }

    private boolean isRightSequence(List<Answer> answers) {
        for (int i = 0; i < answers.size() - 1; i++) {
            int currentAnswerId = answers.get(i).getId();
            int nextAnswerId = answers.get(i + 1).getId();
            if (this.answerDao.findById(currentAnswerId).getNextAnswerId() != this.answerDao.findById(nextAnswerId).getNextAnswerId()) {
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
