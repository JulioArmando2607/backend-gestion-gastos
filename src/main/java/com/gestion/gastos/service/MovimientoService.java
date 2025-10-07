package com.gestion.gastos.service;

import com.gestion.gastos.model.dto.cardResumenResponse;
import com.gestion.gastos.model.dto.proyección.DashboardProjection;
import com.gestion.gastos.model.entity.Movimiento;
import com.gestion.gastos.model.entity.Usuario;
import com.gestion.gastos.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final AuthService authService;

    public Movimiento guardar(Movimiento movimiento) {
        Usuario usuario = authService.getUsuarioAutenticado();
        movimiento.setUsuario(usuario);
        return movimientoRepository.save(movimiento);
    }

    public List<Movimiento> listarTodos() {
        return movimientoRepository.findByActivoTrue();
    }

    public List<Movimiento> listarPorUsuario() {
        Usuario usuario = authService.getUsuarioAutenticado();
        return movimientoRepository.findByUsuarioIdAndActivoTrueOrderByIdDesc(usuario.getId());
    }

    public void eliminar(Long id) {
        movimientoRepository.findById(id).ifPresent(mov -> {
            mov.setActivo(false);
            movimientoRepository.save(mov);
        });
    }

    public cardResumenResponse cardResumenResponse() {
        Usuario usuario = authService.getUsuarioAutenticado();
        return movimientoRepository.cardResumen(usuario.getId());
     ///   return null;
    }

    public Movimiento actualizar(Long id, Movimiento movimientoActualizado) {
        Movimiento existente = movimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        Usuario usuario = authService.getUsuarioAutenticado();
        existente.setUsuario(usuario); // actualiza el usuario si es necesario
        existente.setTipo(movimientoActualizado.getTipo());
        existente.setMonto(movimientoActualizado.getMonto());
        existente.setDescripcion(movimientoActualizado.getDescripcion());
        existente.setFecha(movimientoActualizado.getFecha());
        existente.setCategoria(movimientoActualizado.getCategoria());

        return movimientoRepository.save(existente);
    }

    public List<DashboardProjection> listarDashboard(Long mes, Long anio) {
        Usuario usuario = authService.getUsuarioAutenticado();
        System.out.printf(String.valueOf(usuario.getId()));
        System.out.printf(" ");
        System.out.printf(String.valueOf(mes));
        System.out.printf(" ");
        System.out.printf(String.valueOf(anio));

        /*    List<DashboardProjection> listarDashboard(@Param("idUsuario") Long idUsuario,
                                              @Param("mes") Long mes,
                                              @Param("anio") Long anio*/
        return  movimientoRepository.listarDashboard(usuario.getId(),mes,anio);

    }
}
