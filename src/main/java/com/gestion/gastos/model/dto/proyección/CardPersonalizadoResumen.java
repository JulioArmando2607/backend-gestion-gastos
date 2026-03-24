package com.gestion.gastos.model.dto.proyección;

public interface  CardPersonalizadoResumen {
    Long getId();
    String getNombre();
    String getColorHex();
    String getDescripcion();
    String getMoneda();
    java.math.BigDecimal getMonto();
    java.math.BigDecimal getIngresos();
    java.math.BigDecimal getGastos();
    java.math.BigDecimal getSaldo();
}
