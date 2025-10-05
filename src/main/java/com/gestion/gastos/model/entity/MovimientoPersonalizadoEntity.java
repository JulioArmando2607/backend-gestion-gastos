package com.gestion.gastos.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "movimientos_personalizado",
        indexes = {
                @Index(name = "idx_mp_card_fecha", columnList = "card_id, fecha"),
                @Index(name = "idx_mp_card_categoria", columnList = "card_id, categoria_id"),
                @Index(name = "idx_mp_card_tipo", columnList = "card_id, tipo")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"card", "categoria"})
public class MovimientoPersonalizadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con cards_personalizado
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id", nullable = false)
    private CardPersonalizadoEntity card;

    // Relación con categorias_personalizado
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaPersonalizadoEntity categoria;

    // Usa el mismo enum que en CategoriaPersonalizadoEntity
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CategoriaPersonalizadoEntity.TipoMovimiento tipo; // INGRESO | GASTO

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto; // siempre positivo

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(length = 255)
    private String nota;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean activo = true;
}
