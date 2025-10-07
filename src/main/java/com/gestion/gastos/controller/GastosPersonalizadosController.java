package com.gestion.gastos.controller;

import com.gestion.gastos.model.dto.*;
import com.gestion.gastos.model.dto.proyección.*;
import com.gestion.gastos.model.entity.CategoriaPersonalizadoEntity;
import com.gestion.gastos.service.GastosPersonalizadosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gastos-personalizados")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Para pruebas con Flutter Web
public class GastosPersonalizadosController {
    private final GastosPersonalizadosService gastosPersonalizadosService;

    @GetMapping("/list-card-personalizados")
    public List<CardPersonalizadoResumen> listarCardsPorUsuario() {
        return gastosPersonalizadosService.listarCardsPorUsuario();
    }
    @PostMapping("/crear-gasto-personalizado")
    public ResponseEntity crearGastoPersonalizado(@RequestBody CrearCardPersonalizadoRequest crearCardPersonalizadoRequest) {
        CardPersonalizadoResponse guardado = gastosPersonalizadosService.crearGastoPersonalizado(crearCardPersonalizadoRequest);
        return ResponseEntity.ok(guardado);
    }
    @PostMapping("/crear-categoria")
    public ResponseEntity crearCategoria(@RequestBody CategoriaPersonalizadoRequest categoria) {
        CategoriaPersonalizadoEntity guardado = gastosPersonalizadosService.crearCategoria(categoria);
        return ResponseEntity.ok(guardado);
    }

    @GetMapping("/lista-categoria-personalizado/{idCard}")
    public List<CategoriaPersonalizadoProjection> listarCategoria(@PathVariable Integer idCard) {
        return gastosPersonalizadosService.listarCategoria(idCard);//.ok(guardado);
    }

    @GetMapping("/lista-categoria-personalizado-tipo/{idCard}/{tipo}")
    public List<CategoriaPersonalizadoProjection> listCategoriaPersonalizadoxTipo(@PathVariable Integer idCard,@PathVariable String tipo) {
        return gastosPersonalizadosService.listCategoriaPersonalizadoxTipo(idCard,tipo);//.ok(guardado);
    }

    @GetMapping("/lista-movimientos-personalizado/{idCard}")
    public List<MovimientoPersonalizadoView> listMovimientoPersonalizado(@PathVariable Integer idCard) {
        return gastosPersonalizadosService.listMovimientoPersonalizado(idCard);
    }

    @GetMapping("/card-personalizado/{idCard}")
    public  CardPersonalizadoResumen CardPersonalizadosxId(@PathVariable Integer idCard) {
        return gastosPersonalizadosService.CardPersonalizadosxId(idCard);
    }

    @GetMapping("/listar-reporte-card/{idCard}")
    public List<ReporteMovimientoPersonalizadoView> listarReporteCard(@PathVariable Integer idCard) {
        return gastosPersonalizadosService.listarReporteCard(idCard);
    }


    @PostMapping("/nuevo-gasto")
    public ApiOutResponseDto nuevoGasto(@RequestBody MovimientoPersonalizado movimientoPersonalizado) {

        return  gastosPersonalizadosService.nuevoGasto(movimientoPersonalizado) ;
    }

    @DeleteMapping("eliminar-movimiento/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        gastosPersonalizadosService.eliminarMoviento(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/obtener-movimiento-personalizado/{idMovimiento}")
    public MovimientoPersonalizadoView obtenerMovimientoPersonalizado(@PathVariable Long idMovimiento) {
        return gastosPersonalizadosService.obtenerMovimientoPersonalizado(idMovimiento);
    }

}
