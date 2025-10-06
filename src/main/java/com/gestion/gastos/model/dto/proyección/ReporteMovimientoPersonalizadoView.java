package com.gestion.gastos.model.dto.proyección;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ReporteMovimientoPersonalizadoView {
    String getMes();
    String getTipo();
    BigDecimal getMonto();
    LocalDate getFecha();   // Hibernate convierte DATE → LocalDate
    String getItem();
    String getCantidadMovimientos();
}
