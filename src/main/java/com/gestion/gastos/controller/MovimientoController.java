package com.gestion.gastos.controller;

import com.gestion.gastos.model.dto.ApiOutResponseDto;
import com.gestion.gastos.model.dto.cardResumenResponse;
import com.gestion.gastos.model.dto.proyección.DashboardProjection;
import com.gestion.gastos.model.entity.Movimiento;
import com.gestion.gastos.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MovimientoController {

    private final MovimientoService movimientoService;

    @PostMapping
    public ResponseEntity<Movimiento> registrar(@RequestBody Movimiento movimiento) {
        Movimiento guardado = movimientoService.guardar(movimiento);
        return ResponseEntity.ok(guardado);
    }

    @GetMapping
    public ResponseEntity<List<Movimiento>> listarTodos() {
        return ResponseEntity.ok(movimientoService.listarTodos());
    }

    @GetMapping("/usuario")
    public ResponseEntity<List<Movimiento>> listarPorUsuario() {
        return ResponseEntity.ok(movimientoService.listarPorUsuario());
    }

    @GetMapping("/cardResumen")
    public cardResumenResponse cardResumenResponse() {
        return movimientoService.cardResumenResponse();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        movimientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movimiento> actualizar(@PathVariable Long id, @RequestBody Movimiento movimientoActualizado) {
        Movimiento actualizado = movimientoService.actualizar(id, movimientoActualizado);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/listar-dashboard/{anio}/{mes}")
    public List<DashboardProjection> listarDashboard(@PathVariable Long anio, @PathVariable Long mes) {
        return movimientoService.listarDashboard(mes, anio);
    }

    @GetMapping("/reportes/descripcion")
    public ResponseEntity<ApiOutResponseDto> reportePorDescripcion(
            @RequestParam String tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes
    ) {
        return ResponseEntity.ok(
                movimientoService.reportePorDescripcion(tipo, fechaInicio, fechaFin, anio, mes)
        );
    }

    @GetMapping("/reportes/categoria")
    public ResponseEntity<ApiOutResponseDto> reportePorCategoria(
            @RequestParam String tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes
    ) {
        return ResponseEntity.ok(
                movimientoService.reportePorCategoria(tipo, fechaInicio, fechaFin, anio, mes)
        );
    }
}
