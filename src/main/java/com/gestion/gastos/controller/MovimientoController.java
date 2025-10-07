package com.gestion.gastos.controller;

import com.gestion.gastos.model.dto.cardResumenResponse;
import com.gestion.gastos.model.dto.proyección.DashboardProjection;
import com.gestion.gastos.model.entity.Movimiento;
import com.gestion.gastos.model.entity.Usuario;
import com.gestion.gastos.service.AuthService;
import com.gestion.gastos.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Para pruebas con Flutter Web
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
    public  List<DashboardProjection> listarDashboard(@PathVariable Long anio, @PathVariable Long mes) {
        return movimientoService.listarDashboard(mes,anio);
    }
}
