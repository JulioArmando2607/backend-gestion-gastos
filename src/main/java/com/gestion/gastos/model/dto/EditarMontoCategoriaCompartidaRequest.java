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
public class EditarMontoCategoriaCompartidaRequest {
    private Integer usuarioIdAccion;
    private Integer idProyeccion;
    private Integer idCategoria;
    private BigDecimal montoCategoria;
}
