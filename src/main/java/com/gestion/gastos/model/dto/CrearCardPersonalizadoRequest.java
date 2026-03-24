package com.gestion.gastos.model.dto;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;

@Data
public class CrearCardPersonalizadoRequest {
    @NotNull
 //   private Long userId;

    private String nombre;

    private String descripcion;

    private String moneda = "PEN";

    private String colorHex = "#6C63FF";

    private BigDecimal monto;
}
