package com.gestion.gastos.controller;

import com.gestion.gastos.model.dto.ApiOutResponseDto;
import com.gestion.gastos.model.dto.ProyeccionCategoria;
import com.gestion.gastos.model.dto.proyección.CategoriasProyeccionProjection;
import com.gestion.gastos.model.entity.Movimiento;
import com.gestion.gastos.service.GastosPersonalizadosService;
import com.gestion.gastos.service.ProyeccionMensualService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proyeccion-mensual")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Para pruebas con Flutter Web
public class ProyeccionMensualController {
    private final ProyeccionMensualService service;

    /**
     * GET /api/proyeccion/{usuarioId}/{anio}/{mes}
     * Obtener categorías con sus montos para un periodo específico
     */
    @GetMapping("/categorias/{usuarioId}/{anio}/{mes}")
    public ResponseEntity<List<CategoriasProyeccionProjection>> listarCategorias(
            @PathVariable Integer usuarioId,
            @PathVariable Integer anio,
            @PathVariable Integer mes) {

        List<CategoriasProyeccionProjection> categorias =
                service.listarCategoriasProyeccion(usuarioId, anio, mes);

        return ResponseEntity.ok(categorias);
    }

    /**
     * POST /api/proyeccion/{usuarioId}
     * Guardar o actualizar monto de una categoría
     */
    @PostMapping("/{usuarioId}")
    public ResponseEntity<ApiOutResponseDto> guardarProyeccion(
            @PathVariable Integer usuarioId,
            @RequestBody ProyeccionCategoria proyeccionCategoria) {

        ApiOutResponseDto response =
                service.guardarProyeccionCategoria(proyeccionCategoria, usuarioId);

        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/proyeccion/cerrar/{proyeccionId}
     * Cerrar una proyección (no se podrá editar más)
     */
    @PutMapping("/cerrar/{proyeccionId}")
    public ResponseEntity<ApiOutResponseDto> cerrarProyeccion(
            @PathVariable Integer proyeccionId) {

        ApiOutResponseDto response = service.cerrarProyeccion(proyeccionId);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/proyeccion/categorias/{usuarioId}
     * Crear categorías predeterminadas para un usuario nuevo
     */
    @PostMapping("/categorias/{usuarioId}")
    public ResponseEntity<ApiOutResponseDto> crearCategoriasPredeterminadas(
            @PathVariable Integer usuarioId) {

        ApiOutResponseDto response = new ApiOutResponseDto();
        try {
            service.crearCategoriasPredeterminadas(usuarioId);
            response.setCodResultado(1);
            response.setMsgResultado("Categorías creadas exitosamente");
        } catch (Exception e) {
            response.setCodResultado(0);
            response.setMsgResultado("Error: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}