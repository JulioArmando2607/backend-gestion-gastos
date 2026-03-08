package com.gestion.gastos.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonaUsuarioResponse {
    private Integer id;
    private String celular;
    private LocalDate fechaNacimiento;
    private String preguntaRecuperacion;
    private String respuestaRecuperacion;
    private Boolean activo;
    private LocalDateTime fechaReg;
    private Integer usuarioId;
    private String nombre;
    private String email;
}
