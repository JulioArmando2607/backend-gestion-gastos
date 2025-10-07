package com.gestion.gastos.model.dto.proyección;

import java.math.BigDecimal;

public interface DashboardProjection {
    Long getMesNum();
    String getMesTexto();
    BigDecimal getGastoTotal();
}
