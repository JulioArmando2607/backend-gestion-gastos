package com.gestion.gastos.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompartirGastoPersonalizadoRequest {
    private Integer usuarioIdAccion;
    private String correoDestinatario;
    private Long idGastoPersonalizado;
    private String permiso;
}
