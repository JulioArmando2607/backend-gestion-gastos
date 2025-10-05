package com.gestion.gastos.model.dto.proyección;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface MovimientoPersonalizadoView {
    Long getId();
    String getTipo();
    BigDecimal getMonto();
    LocalDate getFecha();   // Hibernate convierte DATE → LocalDate
    String getNota();
    String getCategoria();
}
