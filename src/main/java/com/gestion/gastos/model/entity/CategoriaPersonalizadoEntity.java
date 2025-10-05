package com.gestion.gastos.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "categorias_personalizado",
        uniqueConstraints = @UniqueConstraint(columnNames = {"card_id", "nombre"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaPersonalizadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_id", nullable = false)
    private Long cardId;                       // << FK sin relación

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column( nullable = true, length = 60)
    private String tipo;

    @Column(name = "color_hex", length = 7)
    private String colorHex;

    @Column(length = 40)
    private String icono;

    @Builder.Default
    @Column(nullable = false)
    private Integer orden = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activa = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public enum TipoMovimiento { INGRESO, GASTO }
}
