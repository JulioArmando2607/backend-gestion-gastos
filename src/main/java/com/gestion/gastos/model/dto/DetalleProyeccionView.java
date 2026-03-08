package com.gestion.gastos.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface DetalleProyeccionView {
    Integer getIdDetalle();
    Integer getIdProyeccion();
    Integer getIdCategoria();
    String getNombreCategoria();
    String getColorCategoria();
    BigDecimal getMontoProyectado();
    BigDecimal getMontoReal();
    String getNotas();
    Integer getAnio();
    Integer getMes();
    LocalDateTime getFechaCreacion();
    LocalDateTime getFechaActualizacion();
}
