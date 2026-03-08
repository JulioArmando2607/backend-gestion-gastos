package com.gestion.gastos.controller;

import com.gestion.gastos.model.dto.ApiOutResponseDto;
import com.gestion.gastos.model.dto.CompartirProyeccionRequest;
import com.gestion.gastos.model.dto.EditarMontoCategoriaCompartidaRequest;
import com.gestion.gastos.service.CompartirProyeccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compartir-proyeccion")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CompartirProyeccionController {

    private final CompartirProyeccionService compartirProyeccionService;

    @PostMapping
    public ResponseEntity<ApiOutResponseDto> compartir(@RequestBody CompartirProyeccionRequest request) {
        return ResponseEntity.ok(compartirProyeccionService.compartir(request));
    }

    @GetMapping("/recibidas/{idPersona}")
    public ResponseEntity<ApiOutResponseDto> listarRecibidas(@PathVariable Integer idPersona) {
        return ResponseEntity.ok(compartirProyeccionService.listarRecibidas(idPersona));
    }

    @GetMapping("/enviadas/{idPersona}")
    public ResponseEntity<ApiOutResponseDto> listarEnviadas(@PathVariable Integer idPersona) {
        return ResponseEntity.ok(compartirProyeccionService.listarEnviadas(idPersona));
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<ApiOutResponseDto> desactivar(@PathVariable Integer id) {
        return ResponseEntity.ok(compartirProyeccionService.desactivar(id));
    }

    @GetMapping("/ver-proyeccion/{idProyeccion}")
    public ResponseEntity<ApiOutResponseDto> verProyeccion(@PathVariable Integer idProyeccion) {
        return ResponseEntity.ok(compartirProyeccionService.verProyeccion(idProyeccion));
    }
    @GetMapping("/detalle-proyeccion/{idProyeccion}")
    public ResponseEntity<ApiOutResponseDto> detalleProyeccion(
            @PathVariable Integer idProyeccion
    ) {
        ApiOutResponseDto response =
                compartirProyeccionService.detalleProyeccion(idProyeccion);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/editar-monto-categoria")
    public ResponseEntity<ApiOutResponseDto> editarMontoCategoria(
            @RequestBody EditarMontoCategoriaCompartidaRequest request
    ) {
        return ResponseEntity.ok(compartirProyeccionService.editarMontoCategoria(request));
    }


}
