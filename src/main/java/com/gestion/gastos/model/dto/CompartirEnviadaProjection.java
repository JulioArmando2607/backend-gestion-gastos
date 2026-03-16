package com.gestion.gastos.model.dto;

import java.time.LocalDateTime;

public interface CompartirEnviadaProjection {
    Integer getIdCompartir();
    Integer getIdProyeccion();
    Integer getOwnerUserId();
    Integer getIdPersonaCompartida();
    String getNombrePersonaCompartida();
    String getCorreoPersonaCompartida();
    Integer getAnio();
    Integer getMes();
    LocalDateTime getFechaCompartido();
}
