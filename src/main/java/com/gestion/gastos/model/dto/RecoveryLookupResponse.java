package com.gestion.gastos.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecoveryLookupResponse {
    private boolean accountFound;
    private boolean hasRecoveryData;
    private String preguntaRecuperacion;
    private String message;
}
