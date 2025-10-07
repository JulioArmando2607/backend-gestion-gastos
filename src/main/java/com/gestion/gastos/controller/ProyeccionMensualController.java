package com.gestion.gastos.controller;

import com.gestion.gastos.model.dto.proyección.CategoriasProyeccionProjection;
import com.gestion.gastos.model.entity.Movimiento;
import com.gestion.gastos.service.GastosPersonalizadosService;
import com.gestion.gastos.service.ProyeccionMensualService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/proyeccion-mensual")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Para pruebas con Flutter Web
public class ProyeccionMensualController {
    private final ProyeccionMensualService proyeccionMensualService;

    @GetMapping
    public List<CategoriasProyeccionProjection> listarCategoriasProyeccionTodos() {

       return proyeccionMensualService.listarCategoriasProyeccionTodos();
    }




}
