package com.gestion.gastos.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GastoPersonalizadoWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    public void broadcast(String payload) {
        sessions.removeIf(session -> !session.isOpen());
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(payload));
            } catch (IOException ignored) {
                try {
                    session.close();
                } catch (IOException closeIgnored) {
                }
            }
        }
    }
}
