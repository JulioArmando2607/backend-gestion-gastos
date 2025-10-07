package com.gestion.gastos.model.dto.proyección;

import java.math.BigDecimal;

public interface CategoriasProyeccionProjection {
    Integer getProyeccionId();
    Integer getCategoriaId();
    String getNombreCategoria();
    String getColorCategoria();
    BigDecimal getMontoCategoria();
    Integer getOrdenCategoria();
    Integer getAnio();
    Integer getMes();
    BigDecimal getIngresoMensual();
    BigDecimal getTotalGasto();
    BigDecimal getAhorroEstimado();
    String getEstado();}
