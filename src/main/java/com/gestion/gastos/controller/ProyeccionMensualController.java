package com.gestion.gastos.controller;

import com.gestion.gastos.model.dto.ApiOutResponseDto;
import com.gestion.gastos.model.dto.ProyeccionCategoria;
import com.gestion.gastos.model.dto.proyección.CategoriasProyeccionProjection;
import com.gestion.gastos.service.ProyeccionMensualService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proyeccion-mensual")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProyeccionMensualController {
    private final ProyeccionMensualService service;

    @GetMapping("/categorias/{usuarioId}/{anio}/{mes}")
    public ResponseEntity<List<CategoriasProyeccionProjection>> listarCategorias(
            @PathVariable Integer usuarioId,
            @PathVariable Integer anio,
            @PathVariable Integer mes) {

        List<CategoriasProyeccionProjection> categorias =
                service.listarCategoriasProyeccion(usuarioId, anio, mes);

        return ResponseEntity.ok(categorias);
    }

    @PostMapping("/nueva-proyeccion-categoria/{usuarioId}")
    public ResponseEntity<ApiOutResponseDto> guardarProyeccionCategoria(
            @PathVariable Integer usuarioId,
            @RequestBody ProyeccionCategoria proyeccionCategoria) {

        ApiOutResponseDto response =
                service.guardarProyeccionCategoria(proyeccionCategoria, usuarioId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/nueva-proyeccion/{usuarioId}")
    public ResponseEntity<ApiOutResponseDto> guardarProyeccion(
            @PathVariable Integer usuarioId,
            @RequestBody ProyeccionCategoria proyeccionCategoria) {

        ApiOutResponseDto response =
                service.guardarProyeccion(proyeccionCategoria, usuarioId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/detalle-proyeccion/{usuarioId}/{anio}/{mes}")
    public ResponseEntity<ApiOutResponseDto> detalleProyeccion(
            @PathVariable Integer usuarioId,
            @PathVariable Integer anio,
            @PathVariable Integer mes) {

        ApiOutResponseDto response = service.detalleProyeccion(usuarioId, anio, mes);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cerrar/{usuarioId}/{anio}/{mes}")
    public ResponseEntity<ApiOutResponseDto> cerrarProyeccion(
            @PathVariable Integer usuarioId,
            @PathVariable Integer anio,
            @PathVariable Integer mes) {

        ApiOutResponseDto response = service.cerrarProyeccion(usuarioId, anio, mes);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/categorias/{usuarioId}")
    public ResponseEntity<ApiOutResponseDto> crearCategoriasPredeterminadas(
            @PathVariable Integer usuarioId) {

        ApiOutResponseDto response = new ApiOutResponseDto();
        try {
            service.crearCategoriasPredeterminadas(usuarioId);
            response.setCodResultado(1);
            response.setMsgResultado("Categorias creadas exitosamente");
        } catch (Exception e) {
            response.setCodResultado(0);
            response.setMsgResultado("Error: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}
