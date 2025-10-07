package com.gestion.gastos.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Entidad ProyeccionMensual
@Entity
@Table(name = "proyeccion_mensual")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyeccionMensual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false)
    private Integer mes;

    @Column(name = "ingreso_mensual", precision = 10, scale = 2, nullable = false)
    private BigDecimal ingresoMensual;

    @Column(name = "total_gastos", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalGastos;

    @Column(name = "ahorro_estimado", precision = 10, scale = 2, nullable = false)
    private BigDecimal ahorroEstimado;

    @Column(nullable = false)
    private String estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

}