package com.gestion.gastos.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditarCardPersonalizadoRequest {
    private String nombre;
    private String descripcion;
    private String moneda;
    private String colorHex;
    private BigDecimal monto;
}

