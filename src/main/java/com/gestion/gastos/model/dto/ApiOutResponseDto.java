package com.gestion.gastos.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Setter
@Getter
public class ApiOutResponseDto {
    private Integer codResultado;
    private String msgResultado;
    private BigDecimal total;
    private Object response;
    private Object objeto;
    private Object aError;

    public ApiOutResponseDto( ) {

    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void setTotal(Long total) {
        this.total = total == null ? null : BigDecimal.valueOf(total);
    }
}
