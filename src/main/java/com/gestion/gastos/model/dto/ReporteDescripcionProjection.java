package com.gestion.gastos.model.dto;

import java.math.BigDecimal;

public interface ReporteDescripcionProjection {
    String getDescripcion();
    String getNombre();
    BigDecimal getTotal();
}
