package com.gestion.gastos.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProyeccionCategoria {
    private Long idProyeccion;
    private Long idCategoria;

    private String nombreCategoria;
    private String colorCategoria;
    private BigDecimal montoCategoria;
    private Long ordenCategoria;

    private Long anio;
    private Long mes;
    private BigDecimal ingresoMensual;

    private BigDecimal totalGasto;
    private BigDecimal ahorroEstimado;
}
