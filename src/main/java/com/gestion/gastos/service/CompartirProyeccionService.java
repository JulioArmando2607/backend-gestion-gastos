package com.gestion.gastos.service;

import com.gestion.gastos.model.dto.ApiOutResponseDto;
import com.gestion.gastos.model.dto.ActualizarPermisoCompartidoRequest;
import com.gestion.gastos.model.dto.CompartirEnviadaProjection;
import com.gestion.gastos.model.dto.CompartirProyeccionRequest;
import com.gestion.gastos.model.dto.DetalleProyeccionView;
import com.gestion.gastos.model.dto.EditarMontoCategoriaCompartidaRequest;
import com.gestion.gastos.model.dto.DetalleProyeccionEditResponse;
import com.gestion.gastos.model.entity.DetalleProyeccion;
import com.gestion.gastos.model.entity.CompartirProyeccion;
import com.gestion.gastos.model.entity.Personas;
import com.gestion.gastos.model.entity.ProyeccionMensual;
import com.gestion.gastos.model.entity.Usuario;
import com.gestion.gastos.repository.CompartirProyeccionRepository;
import com.gestion.gastos.repository.DetalleProyeccionRepository;
import com.gestion.gastos.repository.PersonaRepository;
import com.gestion.gastos.repository.ProyeccionMensualRepository;
import com.gestion.gastos.repository.UsuarioRepository;
import com.gestion.gastos.security.UsuarioAutenticadoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompartirProyeccionService {
    private static final int COD_OK = 1;
    private static final int COD_VALIDACION = 1001;
    private static final int COD_NO_ENCONTRADO = 1004;
    private static final int COD_US_ENCONTRADO = 1003;
    private static final int COD_DUPLICADO = 1005;
    private static final int COD_ERROR = 1999;

    private final CompartirProyeccionRepository compartirProyeccionRepository;
    private final ProyeccionMensualRepository proyeccionMensualRepository;
    private final PersonaRepository personaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DetalleProyeccionRepository detalleRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    @Transactional
    public ApiOutResponseDto compartir(CompartirProyeccionRequest req) {
        if (req == null) {
            return build(COD_VALIDACION, "Request es obligatorio", null, 0);
        }
        if (req.getCorreoDestinatario() == null || req.getCorreoDestinatario().isBlank()) {
            return build(COD_VALIDACION, "correoDestinatario es obligatorio", null, 0);
        }
        if (req.getIdProyeccion() == null) {
            return build(COD_VALIDACION, "idProyeccion es obligatorio", null, 0);
        }
        final String permiso = normalizePermiso(req.getPermiso());

        Integer usuarioAutenticadoId = obtenerUsuarioAutenticadoId();
        Optional<Personas> personaAccionOpt = personaRepository.findByUsuarioId(usuarioAutenticadoId);
        if (personaAccionOpt.isEmpty()) {
            return build(COD_NO_ENCONTRADO, "El usuario que comparte no tiene perfil de persona", null, 0);
        }

        Optional<Usuario> usuarioDestinoOpt = usuarioRepository.findByEmail(req.getCorreoDestinatario().trim());
        if (usuarioDestinoOpt.isEmpty()) {
            return build(COD_US_ENCONTRADO, "No existe un usuario con el correo ingresado", null, 0);
        }

        Integer usuarioDestinoId = Math.toIntExact(usuarioDestinoOpt.get().getId());
        if (usuarioDestinoId.equals(usuarioAutenticadoId)) {
            return build(COD_VALIDACION, "No puedes compartir una proyeccion a tu propio correo", null, 0);
        }

        Optional<Personas> personaDestinoOpt = personaRepository.findByUsuarioId(usuarioDestinoId);
        if (personaDestinoOpt.isEmpty()) {
            return build(COD_NO_ENCONTRADO, "El usuario destino no tiene perfil de persona", null, 0);
        }

        Personas personaAccion = personaAccionOpt.get();
        Personas personaDestino = personaDestinoOpt.get();

        boolean proyeccionDelUsuario = proyeccionMensualRepository
                .existsByIdAndUsuarioId(req.getIdProyeccion(), usuarioAutenticadoId);
        if (!proyeccionDelUsuario) {
            return build(COD_NO_ENCONTRADO, "La proyeccion no existe o no pertenece al usuario que comparte", null, 0);
        }

        boolean yaCompartida = compartirProyeccionRepository
                .existsByIdPersonaCompartioAndIdPersonaCompartidaAndIdProyeccionAndActivoTrue(
                        personaAccion.getId(),
                        personaDestino.getId(),
                        req.getIdProyeccion()
                );
        if (yaCompartida) {
            return build(COD_DUPLICADO, "La proyeccion ya fue compartida a esta persona", null, 0);
        }

        try {
            CompartirProyeccion guardado = compartirProyeccionRepository.save(
                    CompartirProyeccion.builder()
                            .idPersonaCompartio(personaAccion.getId())
                            .idPersonaCompartida(personaDestino.getId())
                            .idProyeccion(req.getIdProyeccion())
                            .permiso(permiso)
                            .activo(true)
                            .build()
            );
            return build(COD_OK, "Proyeccion compartida exitosamente", guardado, 1);
        } catch (Exception e) {
            return build(COD_ERROR, "Error al compartir proyeccion: " + e.getMessage(), null, 0);
        }
    }

    public ApiOutResponseDto listarRecibidas(Integer idPersona) {
        if (idPersona == null) {
            return build(COD_VALIDACION, "idPersona es obligatorio", List.of(), 0);
        }
        if (!obtenerUsuarioAutenticadoId().equals(idPersona)) {
            return build(COD_VALIDACION, "No tienes permiso para ver comparticiones de otro usuario", List.of(), 0);
        }

        List<CompartirEnviadaProjection> data = compartirProyeccionRepository.listarRecibidos(idPersona);
        return build(COD_OK, "Listado de proyecciones recibidas", data, data.size());
    }

    public ApiOutResponseDto listarEnviadas(Integer idPersona,Integer idProyeccion) {
        if (idPersona == null) {
            return build(COD_VALIDACION, "idPersona es obligatorio", List.of(), 0);
        }
        if (!obtenerUsuarioAutenticadoId().equals(idPersona)) {
            return build(COD_VALIDACION, "No tienes permiso para ver comparticiones de otro usuario", List.of(), 0);
        }
        List<CompartirEnviadaProjection> data = compartirProyeccionRepository.listarEnviadasDetalle(idPersona, idProyeccion);
        return build(COD_OK, "Listado de proyecciones enviadas", data, data.size());
    }

    @Transactional
    public ApiOutResponseDto desactivar(Integer idCompartirProyeccion) {
        if (idCompartirProyeccion == null) {
            return build(COD_VALIDACION, "id es obligatorio", null, 0);
        }
        Optional<Personas> personaOpt = personaRepository.findByUsuarioId(obtenerUsuarioAutenticadoId());
        if (personaOpt.isEmpty()) {
            return build(COD_NO_ENCONTRADO, "El usuario autenticado no tiene perfil de persona", null, 0);
        }
        Optional<CompartirProyeccion> registroOpt = compartirProyeccionRepository.findById(idCompartirProyeccion);
        if (registroOpt.isEmpty()) {
            return build(COD_NO_ENCONTRADO, "No existe el registro de comparticion", null, 0);
        }
        if (!compartirProyeccionRepository.existsByIdAndIdPersonaCompartioAndActivoTrue(
                idCompartirProyeccion,
                personaOpt.get().getId()
        )) {
            return build(COD_VALIDACION, "No tienes permiso para desactivar esta comparticion", null, 0);
        }

        try {
            CompartirProyeccion registro = registroOpt.get();
            registro.setActivo(false);
            compartirProyeccionRepository.save(registro);
            return build(COD_OK, "Comparticion desactivada exitosamente", registro, 1);
        } catch (Exception e) {
            return build(COD_ERROR, "Error al desactivar comparticion: " + e.getMessage(), null, 0);
        }
    }

    @Transactional
    public ApiOutResponseDto actualizarPermiso(Integer idCompartir, ActualizarPermisoCompartidoRequest req) {
        if (idCompartir == null) {
            return build(COD_VALIDACION, "id es obligatorio", null, 0);
        }
        if (req == null) {
            return build(COD_VALIDACION, "Request es obligatorio", null, 0);
        }

        Optional<Personas> personaOpt = personaRepository.findByUsuarioId(obtenerUsuarioAutenticadoId());
        if (personaOpt.isEmpty()) {
            return build(COD_NO_ENCONTRADO, "El usuario autenticado no tiene perfil de persona", null, 0);
        }

        Optional<CompartirProyeccion> registroOpt = compartirProyeccionRepository.findById(idCompartir);
        if (registroOpt.isEmpty()) {
            return build(COD_NO_ENCONTRADO, "No existe el registro de comparticion", null, 0);
        }

        CompartirProyeccion registro = registroOpt.get();
        if (!Boolean.TRUE.equals(registro.getActivo())) {
            return build(COD_VALIDACION, "Este acceso ya no está activo", null, 0);
        }
        if (!registro.getIdPersonaCompartio().equals(personaOpt.get().getId())) {
            return build(COD_VALIDACION, "No tienes permiso para cambiar este acceso", null, 0);
        }

        try {
            registro.setPermiso(normalizePermiso(req.getPermiso()));
            compartirProyeccionRepository.save(registro);
            return build(COD_OK, "Permiso actualizado correctamente", registro, 1);
        } catch (Exception e) {
            return build(COD_ERROR, "Error al actualizar permiso: " + e.getMessage(), null, 0);
        }
    }

    private ApiOutResponseDto build(Integer cod, String msg, Object data, Integer total) {
        ApiOutResponseDto out = new ApiOutResponseDto();
        out.setCodResultado(cod);
        out.setMsgResultado(msg);
        out.setResponse(data);
        out.setTotal(BigDecimal.valueOf(total));
        return out;
    }

    public ApiOutResponseDto verProyeccion(Integer idProyeccion) {
        ApiOutResponseDto apiOutResponseDto = new ApiOutResponseDto();

        if (!tieneAccesoProyeccion(idProyeccion)) {
            apiOutResponseDto.setCodResultado(COD_VALIDACION);
            apiOutResponseDto.setMsgResultado("No tienes permiso para ver esta proyeccion");
            apiOutResponseDto.setTotal(BigDecimal.valueOf(0));
            apiOutResponseDto.setResponse(null);
            return apiOutResponseDto;
        }

        Optional<ProyeccionMensual> proyeccionOpt = proyeccionMensualRepository.findById(idProyeccion);

        if (proyeccionOpt.isPresent()) {
            apiOutResponseDto.setCodResultado(COD_OK);
            apiOutResponseDto.setMsgResultado("Proyeccion encontrada");
            apiOutResponseDto.setTotal(BigDecimal.valueOf(1));
            apiOutResponseDto.setResponse(proyeccionOpt.get());
        } else {
            apiOutResponseDto.setCodResultado(COD_NO_ENCONTRADO);
            apiOutResponseDto.setMsgResultado("No existe la proyeccion");
            apiOutResponseDto.setTotal(BigDecimal.valueOf(0));
            apiOutResponseDto.setResponse(null);
        }

        return apiOutResponseDto;
    }

    public ApiOutResponseDto detalleProyeccion(Integer idProyeccion) {
        if (!tieneAccesoProyeccion(idProyeccion)) {
            return build(COD_VALIDACION, "No tienes permiso para ver esta proyeccion", List.of(), 0);
        }

        List<DetalleProyeccionView> detalle = detalleRepository.findDetalleViewByProyeccionId(idProyeccion);

        if (!detalle.isEmpty()) {
            return build(COD_OK, "Detalle de proyeccion encontrado", detalle, detalle.size());
        }
        return build(COD_NO_ENCONTRADO, "No hay detalle para la proyeccion", List.of(), 0);
    }

    @Transactional
    public ApiOutResponseDto editarMontoCategoria(EditarMontoCategoriaCompartidaRequest req) {
        if (req == null) {
            return build(COD_VALIDACION, "Request es obligatorio", null, 0);
        }
        if (req.getIdProyeccion() == null) {
            return build(COD_VALIDACION, "idProyeccion es obligatorio", null, 0);
        }
        if (req.getIdCategoria() == null) {
            return build(COD_VALIDACION, "idCategoria es obligatorio", null, 0);
        }
        if (req.getMontoCategoria() == null) {
            return build(COD_VALIDACION, "montoCategoria es obligatorio", null, 0);
        }
        if (req.getMontoCategoria().compareTo(BigDecimal.ZERO) < 0) {
            return build(COD_VALIDACION, "montoCategoria no puede ser negativo", null, 0);
        }

        Optional<ProyeccionMensual> proyeccionOpt = proyeccionMensualRepository.findById(req.getIdProyeccion());
        if (proyeccionOpt.isEmpty()) {
            return build(COD_NO_ENCONTRADO, "No existe la proyeccion", null, 0);
        }

        ProyeccionMensual proyeccion = proyeccionOpt.get();
        if ("CERRADA".equalsIgnoreCase(proyeccion.getEstado())) {
            return build(COD_VALIDACION, "La proyeccion esta cerrada y no se puede editar", null, 0);
        }

        Integer usuarioAutenticadoId = obtenerUsuarioAutenticadoId();
        boolean esPropietario = proyeccion.getUsuarioId().equals(usuarioAutenticadoId);
        boolean tieneAccesoCompartido = false;

        if (!esPropietario) {
            Optional<Personas> personaOpt = personaRepository.findByUsuarioId(usuarioAutenticadoId);
            if (personaOpt.isPresent()) {
                tieneAccesoCompartido =
                        compartirProyeccionRepository.countAccessByPermiso(
                                personaOpt.get().getId(),
                                req.getIdProyeccion(),
                                "EDITAR"
                        ) > 0;
            }
        }

        if (!esPropietario && !tieneAccesoCompartido) {
            return build(COD_VALIDACION, "No tienes permiso para editar esta proyeccion", null, 0);
        }

        Optional<DetalleProyeccion> detalleOpt = detalleRepository.findByProyeccionIdAndCategoriaId(
                req.getIdProyeccion(),
                req.getIdCategoria()
        );
        if (detalleOpt.isEmpty()) {
            return build(COD_NO_ENCONTRADO, "No existe detalle para la categoria indicada", null, 0);
        }

        try {
            DetalleProyeccion detalle = detalleOpt.get();
            detalle.setMontoProyectado(req.getMontoCategoria());
            detalle.setFechaActualizacion(LocalDateTime.now());
            detalleRepository.save(detalle);

            List<DetalleProyeccion> detalles = detalleRepository.findByProyeccionId(req.getIdProyeccion());
            BigDecimal totalGastos = detalles.stream()
                    .map(DetalleProyeccion::getMontoProyectado)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            proyeccion.setTotalGastos(totalGastos);
            proyeccion.setAhorroEstimado(proyeccion.getIngresoMensual().subtract(totalGastos));
            proyeccion.setFechaActualizacion(LocalDateTime.now());
            proyeccionMensualRepository.save(proyeccion);

            DetalleProyeccionEditResponse response = DetalleProyeccionEditResponse.builder()
                    .idDetalle(detalle.getId())
                    .idProyeccion(req.getIdProyeccion())
                    .idCategoria(req.getIdCategoria())
                    .montoProyectado(detalle.getMontoProyectado())
                    .montoReal(detalle.getMontoReal())
                    .anio(detalle.getAnio())
                    .mes(detalle.getMes())
                    .fechaActualizacion(detalle.getFechaActualizacion())
                    .build();

            return build(COD_OK, "Monto de categoria actualizado exitosamente", response, 1);
        } catch (Exception e) {
            return build(COD_ERROR, "Error al actualizar monto de categoria: " + e.getMessage(), null, 0);
        }
    }

    private boolean tieneAccesoProyeccion(Integer idProyeccion) {
        Optional<ProyeccionMensual> proyeccionOpt = proyeccionMensualRepository.findById(idProyeccion);
        if (proyeccionOpt.isEmpty()) {
            return false;
        }

        Integer usuarioAutenticadoId = obtenerUsuarioAutenticadoId();
        ProyeccionMensual proyeccion = proyeccionOpt.get();
        if (proyeccion.getUsuarioId().equals(usuarioAutenticadoId)) {
            return true;
        }

        return personaRepository.findByUsuarioId(usuarioAutenticadoId)
                .map(persona -> compartirProyeccionRepository
                        .existsByIdPersonaCompartidaAndIdProyeccionAndActivoTrue(persona.getId(), idProyeccion))
                .orElse(false);
    }

    private Integer obtenerUsuarioAutenticadoId() {
        return usuarioAutenticadoService.obtenerUsuarioIdComoInteger();
    }

    private String normalizePermiso(String permiso) {
        if (permiso == null || permiso.isBlank()) {
            return "EDITAR";
        }
        String normalized = permiso.trim().toUpperCase();
        if ("SOLO_VER".equals(normalized) || "VER".equals(normalized)) {
            return "VER";
        }
        if ("PUEDE_EDITAR".equals(normalized) || "EDITAR".equals(normalized)) {
            return "EDITAR";
        }
        throw new IllegalArgumentException("Permiso inválido");
    }
}
