package com.gestion.gastos.model.dto;

import java.math.BigDecimal;

public interface ReporteCategoriaProjection {
    Long getId();
    String getNombre();
    BigDecimal getTotal();
}
