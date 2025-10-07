package com.gestion.gastos.model.dto.proyección;

import java.math.BigDecimal;

public interface CategoriasProyeccionProjection {
    String getNombre();
    BigDecimal getMontoProyectado();
    String getColor();
    Long getAnio();
    Long getMes();
}
