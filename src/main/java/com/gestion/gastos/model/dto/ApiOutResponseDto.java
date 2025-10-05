package com.gestion.gastos.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class ApiOutResponseDto {
    private Integer codResultado;
    private String msgResultado;
    private Integer total;
    private Object response;
    private Object objeto;
    private Object aError;

    public ApiOutResponseDto( ) {

    }
}
