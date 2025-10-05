package com.gestion.gastos.model.dto;

import lombok.Data;

@Data

public class CategoriaPersonalizadoRequest {

    //private Long userId;

    private String nombre;

    private String tipoMovimiento;

    private Integer idCard;
}
