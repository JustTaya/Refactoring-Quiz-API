package com.quiz.controllers;

import com.quiz.dto.GameAnswersDto;
import com.quiz.dto.GameQuestionsDto;
import com.quiz.dto.GameSessionDto;
import com.quiz.entities.Player;
import com.quiz.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/quizzes/play")
public class GameController {

    @Autowired
    private GameService gameService;
    @Autowired
    private SimpMessagingTemplate template;

    private static final String GAME_URL = "/quizzes/play/game/%s";

    @PostMapping("/session")
    public int addGameSession(@RequestBody GameSessionDto gameSessionDto) {
        return gameService.addGameSession(gameSessionDto.getQuizId(), gameSessionDto.getHostId(),
                gameSessionDto.getQuestionTimer(), gameSessionDto.getMaxUsersNumber());
    }

    @MessageMapping("/game/{gameId}/user")
    public void userJoinGameSession(@DestinationVariable int gameId, @RequestBody Player player, SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();

        if (sessionAttributes != null) {
            sessionAttributes.put("userId", player.getUserId());
            sessionAttributes.put("gameId", gameId);
        }

        template.convertAndSend(String.format(GAME_URL, gameId), gameService.addUserInSession(gameId, player));
    }

    @MessageMapping("/game/{gameId}/start")
    public void startGame(@DestinationVariable int gameId) {
        this.sendQuestion(gameId, this.gameService.nextQuestion(gameId));
    }

    @MessageMapping("/game/{gameId}/sendAnswer")
    public void receiveAnswer(@DestinationVariable int gameId, @RequestBody GameAnswersDto answers) {
        if (this.gameService.handleAnswer(gameId, answers.getPlayer(), answers)) {
            this.sendQuestion(gameId, this.gameService.nextQuestion(gameId));
        }
    }

    @MessageMapping("/game/{gameId}/finish")
    public void finishGame(@DestinationVariable int gameId) {
        GameSessionDto rating = gameService.deleteGameSession(gameId);
        if (rating != null) {
            template.convertAndSend(String.format(GAME_URL, gameId), rating);
        }
    }

    private void sendQuestion(int gameId, GameQuestionsDto gameQuestionsDto) {
        template.convertAndSend(String.format(GAME_URL, gameId), gameQuestionsDto);
    }

    public void handleUserDisconnection(int userId, int gameId) {
        this.gameService.onUserDisconnection(userId, gameId);
    }
}
