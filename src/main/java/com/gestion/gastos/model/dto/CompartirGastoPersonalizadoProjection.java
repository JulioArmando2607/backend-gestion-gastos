package com.gestion.gastos.model.dto;

import java.time.LocalDateTime;

public interface CompartirGastoPersonalizadoProjection {
    Integer getIdCompartir();
    Long getIdGastoPersonalizado();
    Long getOwnerUserId();
    Integer getIdPersonaCompartida();
    String getNombreGastoPersonalizado();
    String getDescripcion();
    String getMoneda();
    String getColorHex();
    java.math.BigDecimal getIngresos();
    java.math.BigDecimal getGastos();
    java.math.BigDecimal getSaldo();
    String getNombreRelacionada();
    String getCorreoRelacionada();
    String getPermiso();
    LocalDateTime getFechaCompartido();
}
