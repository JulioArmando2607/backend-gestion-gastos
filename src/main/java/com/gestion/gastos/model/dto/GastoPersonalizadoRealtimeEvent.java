package com.gestion.gastos.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GastoPersonalizadoRealtimeEvent {
    private String eventType;
    private Long idGastoPersonalizado;
    private Long ownerUserId;
    private List<Long> targetUserIds;
    private LocalDateTime changedAt;
}
