package com.gestion.gastos.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CategoriaDto {
    private Integer id;
    private String totalGastos;
    private String color;
    private String tipo;
    private String icono;
}
