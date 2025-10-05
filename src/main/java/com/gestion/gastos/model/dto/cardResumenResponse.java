package com.gestion.gastos.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class cardResumenResponse {
    private BigDecimal totalIngresos;
    private BigDecimal totalGastos;
    private BigDecimal saldoTotal;
}
