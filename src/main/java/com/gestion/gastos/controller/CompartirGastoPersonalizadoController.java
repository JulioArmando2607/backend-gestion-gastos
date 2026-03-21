package com.gestion.gastos.controller;

import com.gestion.gastos.model.dto.ApiOutResponseDto;
import com.gestion.gastos.model.dto.ActualizarPermisoCompartidoRequest;
import com.gestion.gastos.model.dto.CompartirGastoPersonalizadoRequest;
import com.gestion.gastos.service.CompartirGastoPersonalizadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compartir-gasto-personalizado")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CompartirGastoPersonalizadoController {

    private final CompartirGastoPersonalizadoService compartirGastoPersonalizadoService;

    @PostMapping
    public ResponseEntity<ApiOutResponseDto> compartir(
            @RequestBody CompartirGastoPersonalizadoRequest request
    ) {
        return ResponseEntity.ok(compartirGastoPersonalizadoService.compartir(request));
    }

    @GetMapping("/recibidos/{idUsuario}")
    public ResponseEntity<ApiOutResponseDto> listarRecibidos(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(compartirGastoPersonalizadoService.listarRecibidos(idUsuario));
    }

    @GetMapping("/enviados/{idUsuario}")
    public ResponseEntity<ApiOutResponseDto> listarEnviados(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(compartirGastoPersonalizadoService.listarEnviados(idUsuario));
    }

    @GetMapping("/card/{idGastoPersonalizado}")
    public ResponseEntity<ApiOutResponseDto> obtenerDetalleCompartido(
            @PathVariable Long idGastoPersonalizado
    ) {
        return ResponseEntity.ok(
                compartirGastoPersonalizadoService.obtenerDetalleCompartido(idGastoPersonalizado)
        );
    }

    @GetMapping("/movimientos/{idGastoPersonalizado}")
    public ResponseEntity<ApiOutResponseDto> listarMovimientosCompartidos(
            @PathVariable Long idGastoPersonalizado
    ) {
        return ResponseEntity.ok(
                compartirGastoPersonalizadoService.listarMovimientosCompartidos(idGastoPersonalizado)
        );
    }

    @GetMapping("/reporte/{idGastoPersonalizado}")
    public ResponseEntity<ApiOutResponseDto> listarReporteCompartido(
            @PathVariable Long idGastoPersonalizado
    ) {
        return ResponseEntity.ok(
                compartirGastoPersonalizadoService.listarReporteCompartido(idGastoPersonalizado)
        );
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<ApiOutResponseDto> desactivar(@PathVariable Integer id) {
        return ResponseEntity.ok(compartirGastoPersonalizadoService.desactivar(id));
    }

    @PutMapping("/{id}/permiso")
    public ResponseEntity<ApiOutResponseDto> actualizarPermiso(
            @PathVariable Integer id,
            @RequestBody ActualizarPermisoCompartidoRequest request
    ) {
        return ResponseEntity.ok(compartirGastoPersonalizadoService.actualizarPermiso(id, request));
    }
}
