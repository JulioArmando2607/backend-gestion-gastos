package com.gestion.gastos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.gastos.model.dto.GastoPersonalizadoRealtimeEvent;
import com.gestion.gastos.repository.CompartirGastoPersonalizadoRepository;
import com.gestion.gastos.websocket.GastoPersonalizadoWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GastoPersonalizadoRealtimeNotifier {

    private final CompartirGastoPersonalizadoRepository compartirRepository;
    private final GastoPersonalizadoWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    public void notifyChange(String eventType, Long idGastoPersonalizado, Long ownerUserId) {
        List<Long> targetUserIds = new ArrayList<>();
        targetUserIds.add(ownerUserId);
        targetUserIds.addAll(compartirRepository.findRecipientUserIdsByGastoPersonalizadoId(idGastoPersonalizado));

        GastoPersonalizadoRealtimeEvent event = GastoPersonalizadoRealtimeEvent.builder()
                .eventType(eventType)
                .idGastoPersonalizado(idGastoPersonalizado)
                .ownerUserId(ownerUserId)
                .targetUserIds(targetUserIds.stream().distinct().toList())
                .changedAt(LocalDateTime.now())
                .build();

        try {
            webSocketHandler.broadcast(objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException ignored) {
        }
    }
}
