package com.gestion.gastos.config;

import com.gestion.gastos.websocket.GastoPersonalizadoWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final GastoPersonalizadoWebSocketHandler gastoPersonalizadoWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gastoPersonalizadoWebSocketHandler, "/ws/gastos-personalizados")
                .setAllowedOriginPatterns(
                        "https://cashlyplus.com",
                        "https://www.cashlyplus.com",
                        "https://ambientepruebas.pais.gob.pe",
                        "http://localhost:*",
                        "http://127.0.0.1:*",
                        "http://192.168.*:*",
                        "http://10.*:*",
                        "http://172.16.*:*",
                        "http://172.17.*:*",
                        "http://172.18.*:*",
                        "http://172.19.*:*",
                        "http://172.2*.*:*",
                        "http://172.30.*:*",
                        "http://172.31.*:*"
                );
    }
}
