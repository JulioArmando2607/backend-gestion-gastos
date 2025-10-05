package com.gestion.gastos.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // Ej: Transporte, Sueldo

    private String tipo;   // "GASTO" o "INGRESO"

    private String color;  // Código HEX (opcional)

    private String icono;  // Nombre del ícono

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnoreProperties({"password", "email", "fechaCreacion", "activo"}) // opcional, para evitar exponer datos
    private Usuario usuario;
}
