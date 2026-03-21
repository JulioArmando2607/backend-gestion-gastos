package com.gestion.gastos.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "compartir_gasto_personalizado")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompartirGastoPersonalizado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_persona_compartio", nullable = false)
    private Integer idPersonaCompartio;

    @Column(name = "id_persona_compartida", nullable = false)
    private Integer idPersonaCompartida;

    @Column(name = "gasto_personalizado", nullable = false)
    private Long gastoPersonalizadoId;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "permiso", length = 20)
    @Builder.Default
    private String permiso = "EDITAR";

    @Column(name = "fecha_reg", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaReg;
}
