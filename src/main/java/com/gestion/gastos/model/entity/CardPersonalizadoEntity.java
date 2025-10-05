package com.gestion.gastos.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cards_personalizado",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "nombre"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardPersonalizadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false, length = 3)
    private String moneda = "PEN";

    @Column(name = "color_hex", length = 7)
    private String colorHex;

    @Column(length = 40)
    private String icono;

    @Column(nullable = false)
    private Boolean archivado = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
