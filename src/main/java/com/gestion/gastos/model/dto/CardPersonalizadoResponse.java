package com.gestion.gastos.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// response
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardPersonalizadoResponse {
    private Long id;
    private Long userId;
    private String nombre;
    private String descripcion;
    private String moneda;
    private String colorHex;
    private String icono;
    private boolean archivado;
    private LocalDateTime createdAt;
}