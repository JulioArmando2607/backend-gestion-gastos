package com.gestion.gastos.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "personas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Personas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "celular", length = 20)
    private String celular;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "pregunta_recuperacion", length = 255)
    private String preguntaRecuperacion;

    @Column(name = "respuesta_recuperacion", length = 255)
    private String respuestaRecuperacion;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "fecha_reg")
    private LocalDateTime fechaReg;

    @Column(name = "usuario_id")
    private Integer usuarioId;
}

