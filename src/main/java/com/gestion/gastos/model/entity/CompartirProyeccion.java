package com.gestion.gastos.model.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "compartir_proyeccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompartirProyeccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_persona_compartio", nullable = false)
    private Integer idPersonaCompartio;

    @Column(name = "id_persona_compartida", nullable = false)
    private Integer idPersonaCompartida;

    @Column(name = "id_proyeccion", nullable = false)
    private Integer idProyeccion;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "fecha_reg", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaReg;
}