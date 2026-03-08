package com.gestion.gastos.model.dto;

import java.time.LocalDateTime;

public interface CompartirEnviadaProjection {
    Integer getIdCompartir();
    Integer getIdProyeccion();
    Integer getIdPersonaCompartida();
    String getNombrePersonaCompartida();
    String getCorreoPersonaCompartida();
    LocalDateTime getFechaCompartido();
}
