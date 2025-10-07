package com.gestion.gastos.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Getter
@Setter
public class DashboardDTO {
    private Long mesNum;
    private String mesTexto;
    private BigDecimal gastoTotal;
}
