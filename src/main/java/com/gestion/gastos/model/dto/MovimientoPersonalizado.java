package com.gestion.gastos.model.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@AllArgsConstructor

public class MovimientoPersonalizado {

        private Long idCard;

        private Long idMovimiento;

        private Long categoria;

        private Long usuario;

        private String tipo;

        private BigDecimal monto;

        private String descripcion;

        private LocalDate fecha;
}
