package com.quiz.config;

import com.quiz.controllers.GameController;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@RequiredArgsConstructor
public class WebSocketEventListener {
    private GameController gameController;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if(sessionAttributes != null) {
            int[] userId = (int[]) sessionAttributes.get("userId");
            gameController.handleUserDisconnection(userId[0], userId[1]);
        }
    }
}
