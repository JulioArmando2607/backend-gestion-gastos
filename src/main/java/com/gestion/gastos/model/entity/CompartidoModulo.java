package com.gestion.gastos.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "compartido_modulo",
        indexes = {
                @Index(name = "idx_compartido_modulo_recurso", columnList = "modulo, recurso_id, activo"),
                @Index(name = "idx_compartido_modulo_compartida", columnList = "id_persona_compartida, modulo, activo"),
                @Index(name = "idx_compartido_modulo_compartio", columnList = "id_persona_compartio, modulo, activo")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompartidoModulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_persona_compartio", nullable = false)
    private Integer idPersonaCompartio;

    @Column(name = "id_persona_compartida", nullable = false)
    private Integer idPersonaCompartida;

    @Enumerated(EnumType.STRING)
    @Column(name = "modulo", nullable = false, length = 50)
    private ModuloCompartido modulo;

    @Column(name = "recurso_id", nullable = false)
    private Long recursoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "permiso", nullable = false, length = 20)
    @Builder.Default
    private PermisoCompartido permiso = PermisoCompartido.EDITAR;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "fecha_reg", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaReg;
}
