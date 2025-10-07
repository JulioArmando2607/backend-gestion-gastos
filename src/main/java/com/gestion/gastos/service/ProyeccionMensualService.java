package com.gestion.gastos.service;

import com.gestion.gastos.model.dto.ApiOutResponseDto;
import com.gestion.gastos.model.dto.ProyeccionCategoria;
import com.gestion.gastos.model.dto.proyección.CategoriasProyeccionProjection;
import com.gestion.gastos.model.entity.CategoriaProyeccion;
import com.gestion.gastos.model.entity.DetalleProyeccion;
import com.gestion.gastos.model.entity.ProyeccionMensual;
import com.gestion.gastos.repository.CategoriaProyeccionRepository;
import com.gestion.gastos.repository.DetalleProyeccionRepository;
import com.gestion.gastos.repository.ProyeccionMensualRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ProyeccionMensualService {

    private final ProyeccionMensualRepository proyeccionRepository;
    private final CategoriaProyeccionRepository categoriaRepository;
    private final DetalleProyeccionRepository detalleRepository;
    ///private final ProyeccionCustomRepository proyeccionCustomRepository;
    public List<CategoriasProyeccionProjection> listarCategoriasProyeccion(
            Integer usuarioId,
            Integer anio,
            Integer mes) {

        // Verificar si el usuario tiene categorías, si no, crear las predeterminadas
        if (!categoriaRepository.existsByUsuarioId(usuarioId)) {
            crearCategoriasPredeterminadas(usuarioId);
        }

        // ⭐ Ahora usamos el método directo del repositorio
        return categoriaRepository.findCategoriasConProyeccion(usuarioId, anio, mes);
    }

    // ============================================
    // GUARDAR/ACTUALIZAR PROYECCIÓN Y CATEGORÍA
    // ============================================

    @Transactional
    public ApiOutResponseDto guardarProyeccionCategoria(ProyeccionCategoria dto, Integer usuarioId) {
        ApiOutResponseDto response = new ApiOutResponseDto();

        try {
            // 1. Verificar o crear la proyección mensual
            ProyeccionMensual proyeccion = obtenerOCrearProyeccion(
                    usuarioId,
                    dto.getAnio().intValue(),
                    dto.getMes().intValue(),
                    dto.getIngresoMensual()
            );

            // 2. Verificar si es una categoría nueva o existente
            CategoriaProyeccion categoria;

            if (dto.getIdCategoria() == null || dto.getIdCategoria() == 0) {
                // Crear nueva categoría
                categoria = crearNuevaCategoria(usuarioId, dto);
            } else {
                // Usar categoría existente
                categoria = categoriaRepository.findById(dto.getIdCategoria().intValue())
                        .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            }

            // 3. Guardar o actualizar el detalle de proyección
            DetalleProyeccion detalle = guardarDetalleProyeccion(
                    proyeccion,
                    categoria,
                    dto.getMontoCategoria()
            );

            // 4. Recalcular totales
            recalcularTotales(proyeccion);

            response.setCodResultado(1);
            response.setMsgResultado("Proyección guardada exitosamente");
            response.setResponse(detalle.getId());

        } catch (Exception e) {
            response.setCodResultado(0);
            response.setMsgResultado("Error al guardar proyección: " + e.getMessage());
        }

        return response;
    }

    // ============================================
    // MÉTODOS AUXILIARES
    // ============================================

    private ProyeccionMensual obtenerOCrearProyeccion(
            Integer usuarioId,
            Integer anio,
            Integer mes,
            BigDecimal ingresoMensual) {

        Optional<ProyeccionMensual> proyeccionOpt =
                proyeccionRepository.findByUsuarioIdAndAnioAndMes(usuarioId, anio, mes);

        if (proyeccionOpt.isPresent()) {
            // Actualizar ingreso si cambió
            ProyeccionMensual proyeccion = proyeccionOpt.get();
            if (ingresoMensual != null &&
                    ingresoMensual.compareTo(proyeccion.getIngresoMensual()) != 0) {
                proyeccion.setIngresoMensual(ingresoMensual);
                proyeccion.setFechaActualizacion(LocalDateTime.now());
                proyeccionRepository.save(proyeccion);
            }
            return proyeccion;
        } else {
            // Crear nueva proyección
            ProyeccionMensual nuevaProyeccion = ProyeccionMensual.builder()
                    .usuarioId(usuarioId)
                    .anio(anio)
                    .mes(mes)
                    .ingresoMensual(ingresoMensual != null ? ingresoMensual : BigDecimal.ZERO)
                    .totalGastos(BigDecimal.ZERO)
                    .ahorroEstimado(BigDecimal.ZERO)
                    .estado("ABIERTA")
                    .fechaCreacion(LocalDateTime.now())
                    .fechaActualizacion(LocalDateTime.now())
                    .build();

            return proyeccionRepository.save(nuevaProyeccion);
        }
    }

    private CategoriaProyeccion crearNuevaCategoria(Integer usuarioId, ProyeccionCategoria dto) {
        // Obtener el último orden
        List<CategoriaProyeccion> categorias =
                categoriaRepository.findByUsuarioIdAndActivaTrueOrderByOrden(usuarioId);

        int nuevoOrden = categorias.isEmpty() ? 1 :
                categorias.get(categorias.size() - 1).getOrden() + 1;

        CategoriaProyeccion categoria = CategoriaProyeccion.builder()
                .usuarioId(usuarioId)
                .nombre(dto.getNombreCategoria())
                .color(dto.getColorCategoria() != null ? dto.getColorCategoria() : "#E0E0E0")
                .esPredeterminada(false)
                .orden(nuevoOrden)
                .activa(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        return categoriaRepository.save(categoria);
    }

    private DetalleProyeccion guardarDetalleProyeccion(
            ProyeccionMensual proyeccion,
            CategoriaProyeccion categoria,
            BigDecimal monto) {

        Optional<DetalleProyeccion> detalleOpt =
                detalleRepository.findByProyeccionIdAndCategoriaId(
                        proyeccion.getId(),
                        categoria.getId()
                );

        if (detalleOpt.isPresent()) {
            // Actualizar existente
            DetalleProyeccion detalle = detalleOpt.get();
            detalle.setMontoProyectado(monto);
            detalle.setFechaActualizacion(LocalDateTime.now());
            return detalleRepository.save(detalle);
        } else {
            // Crear nuevo
            DetalleProyeccion nuevoDetalle = DetalleProyeccion.builder()
                    .proyeccion(proyeccion)
                    .categoria(categoria)
                    .montoProyectado(monto)
                    .montoReal(BigDecimal.ZERO)
                    .fechaCreacion(LocalDateTime.now())
                    .fechaActualizacion(LocalDateTime.now())
                    .build();

            return detalleRepository.save(nuevoDetalle);
        }
    }

    private void recalcularTotales(ProyeccionMensual proyeccion) {
        List<DetalleProyeccion> detalles =
                detalleRepository.findByProyeccionId(proyeccion.getId());

        BigDecimal totalGastos = detalles.stream()
                .map(DetalleProyeccion::getMontoProyectado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ahorroEstimado = proyeccion.getIngresoMensual().subtract(totalGastos);

        proyeccion.setTotalGastos(totalGastos);
        proyeccion.setAhorroEstimado(ahorroEstimado);
        proyeccion.setFechaActualizacion(LocalDateTime.now());

        proyeccionRepository.save(proyeccion);
    }

    @Transactional
    public void crearCategoriasPredeterminadas(Integer usuarioId) {
        List<CategoriaProyeccion> categorias = List.of(
                crearCategoria(usuarioId, "Prestamo efectivo", "#E0E0E0", 1),
                crearCategoria(usuarioId, "Pasajes", "#E0E0E0", 2),
                crearCategoria(usuarioId, "Alimentos casa", "#E0E0E0", 3),
                crearCategoria(usuarioId, "Prestamo colegio", "#E0E0E0", 4),
                crearCategoria(usuarioId, "Pago Falabella", "#FFF59D", 5),
                crearCategoria(usuarioId, "Pago Oh", "#FFAB91", 6),
                crearCategoria(usuarioId, "Pago Movistar", "#81C784", 7),
                crearCategoria(usuarioId, "Servicios Basicos", "#E0E0E0", 8),
                crearCategoria(usuarioId, "Teléfono", "#FFF9C4", 9),
                crearCategoria(usuarioId, "Roto", "#E0E0E0", 10),
                crearCategoria(usuarioId, "Add", "#E0E0E0", 11),
                crearCategoria(usuarioId, "Viejo", "#E0E0E0", 12),
                crearCategoria(usuarioId, "Internet", "#E0E0E0", 13)
        );

        categoriaRepository.saveAll(categorias);
    }

    private CategoriaProyeccion crearCategoria(Integer usuarioId, String nombre, String color, int orden) {
        return CategoriaProyeccion.builder()
                .usuarioId(usuarioId)
                .nombre(nombre)
                .color(color)
                .esPredeterminada(true)
                .orden(orden)
                .activa(true)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }
    public ApiOutResponseDto cerrarProyeccion(Integer proyeccionId) {
        return  null;
    }
}
