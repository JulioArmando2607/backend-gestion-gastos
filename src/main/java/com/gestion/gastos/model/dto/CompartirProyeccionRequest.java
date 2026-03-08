package com.gestion.gastos.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompartirProyeccionRequest {
    private Integer usuarioIdAccion;
    private String correoDestinatario;
    private Integer idProyeccion;
}
