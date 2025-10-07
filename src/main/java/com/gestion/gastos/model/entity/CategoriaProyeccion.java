package com.gestion.gastos.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "categorias_proyeccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaProyeccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 7)
    private String color;

    @Column(name = "es_predeterminada", nullable = false)
    private Boolean esPredeterminada;

    @Column(nullable = false)
    private Integer orden;

    @Column(nullable = false)
    private Boolean activa;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

}