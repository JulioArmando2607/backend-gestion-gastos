package com.gestion.gastos.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;

    private BigDecimal monto;

    private String descripcion;

    private LocalDate fecha;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    @JsonIgnoreProperties({"usuario"}) // 👈 evita cargar el usuario dentro de categoría
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnoreProperties({"password", "email", "fechaCreacion", "activo"}) // opcional, para evitar exponer datos
    private Usuario usuario;

    private Boolean activo = true; // 👈 Nuevo campo para soft delete

}
