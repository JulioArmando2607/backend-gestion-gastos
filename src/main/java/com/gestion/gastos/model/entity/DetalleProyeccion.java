package com.gestion.gastos.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "detalle_proyeccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleProyeccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proyeccion", nullable = false)
    private ProyeccionMensual proyeccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private CategoriaProyeccion categoria;

    @Column(name = "monto_proyectado", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoProyectado;


    @Column(name = "monto_real", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoReal;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}

