package com.gestion.gastos.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleProyeccionEditResponse {
    private Integer idDetalle;
    private Integer idProyeccion;
    private Integer idCategoria;
    private BigDecimal montoProyectado;
    private BigDecimal montoReal;
    private Integer anio;
    private Integer mes;
    private LocalDateTime fechaActualizacion;
}
