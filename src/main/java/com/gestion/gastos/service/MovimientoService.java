package com.gestion.gastos.service;

import com.gestion.gastos.model.dto.ApiOutResponseDto;
import com.gestion.gastos.model.dto.ReporteCategoriaProjection;
import com.gestion.gastos.model.dto.ReporteDescripcionProjection;
import com.gestion.gastos.model.dto.cardResumenResponse;
import com.gestion.gastos.model.dto.proyección.DashboardProjection;
import com.gestion.gastos.model.entity.Movimiento;
import com.gestion.gastos.model.entity.Usuario;
import com.gestion.gastos.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private static final int COD_OK = 1;
    private static final int COD_VALIDACION = 1001;

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
        return movimientoRepository.findByUsuarioIdAndActivoTrueOrderByFechaDescCreadoEnDesc(usuario.getId());
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
    }

    public Movimiento actualizar(Long id, Movimiento movimientoActualizado) {
        Movimiento existente = movimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        Usuario usuario = authService.getUsuarioAutenticado();
        existente.setUsuario(usuario);
        existente.setTipo(movimientoActualizado.getTipo());
        existente.setMonto(movimientoActualizado.getMonto());
        existente.setDescripcion(movimientoActualizado.getDescripcion());
        existente.setFecha(movimientoActualizado.getFecha());
        existente.setCategoria(movimientoActualizado.getCategoria());

        return movimientoRepository.save(existente);
    }

    public List<DashboardProjection> listarDashboard(Long mes, Long anio) {
        Usuario usuario = authService.getUsuarioAutenticado();
        return movimientoRepository.listarDashboard(usuario.getId(), mes, anio);
    }

    public ApiOutResponseDto reportePorDescripcion(
            String tipo,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Integer anio,
            Integer mes
    ) {
        try {
            validarFiltros(fechaInicio, fechaFin, anio, mes);
            Usuario usuario = authService.getUsuarioAutenticado();
            List<ReporteDescripcionProjection> data = movimientoRepository.reportePorDescripcion(
                    usuario.getId(),
                    normalizarTipo(tipo),
                    fechaInicio,
                    fechaFin,
                    anio,
                    mes
            );
            return build(COD_OK, "Reporte por descripcion obtenido correctamente", data, sumarTotalDescripcion(data));
        } catch (IllegalArgumentException ex) {
            return build(COD_VALIDACION, ex.getMessage(), List.of(), BigDecimal.ZERO.setScale(2));
        }
    }

    public ApiOutResponseDto reportePorCategoria(
            String tipo,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Integer anio,
            Integer mes
    ) {
        try {
            validarFiltros(fechaInicio, fechaFin, anio, mes);
            Usuario usuario = authService.getUsuarioAutenticado();
            List<ReporteCategoriaProjection> data = movimientoRepository.reportePorCategoria(
                    usuario.getId(),
                    normalizarTipo(tipo),
                    fechaInicio,
                    fechaFin,
                    anio,
                    mes
            );
            return build(COD_OK, "Reporte por categoria obtenido correctamente", data, sumarTotalCategoria(data));
        } catch (IllegalArgumentException ex) {
            return build(COD_VALIDACION, ex.getMessage(), List.of(), BigDecimal.ZERO.setScale(2));
        }
    }

    private String normalizarTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("tipo es obligatorio");
        }

        String tipoNormalizado = tipo.trim().toUpperCase(Locale.ROOT);
        if (!"GASTO".equals(tipoNormalizado) && !"INGRESO".equals(tipoNormalizado)) {
            throw new IllegalArgumentException("tipo debe ser GASTO o INGRESO");
        }
        return tipoNormalizado;
    }

    private void validarFiltros(LocalDate fechaInicio, LocalDate fechaFin, Integer anio, Integer mes) {
        boolean usaRango = fechaInicio != null || fechaFin != null;
        boolean usaMes = anio != null || mes != null;

        if (usaRango && usaMes) {
            throw new IllegalArgumentException("Usa rango de fechas o mes/anio, no ambos");
        }

        if (usaRango && (fechaInicio == null || fechaFin == null)) {
            throw new IllegalArgumentException("fechaInicio y fechaFin son obligatorios juntos");
        }

        if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("fechaInicio no puede ser mayor que fechaFin");
        }

        if (usaMes && (anio == null || mes == null)) {
            throw new IllegalArgumentException("anio y mes son obligatorios juntos");
        }

        if (mes != null && (mes < 1 || mes > 12)) {
            throw new IllegalArgumentException("mes debe estar entre 1 y 12");
        }
    }

    private BigDecimal sumarTotalDescripcion(List<ReporteDescripcionProjection> data) {
        BigDecimal total = data.stream()
                .map(ReporteDescripcionProjection::getTotal)
                .filter(valor -> valor != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private BigDecimal sumarTotalCategoria(List<ReporteCategoriaProjection> data) {
        BigDecimal total = data.stream()
                .map(ReporteCategoriaProjection::getTotal)
                .filter(valor -> valor != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private ApiOutResponseDto build(Integer cod, String msg, Object data, BigDecimal total) {
        ApiOutResponseDto out = new ApiOutResponseDto();
        out.setCodResultado(cod);
        out.setMsgResultado(msg);
        out.setResponse(data);
        out.setTotal(total);
        return out;
    }
}
