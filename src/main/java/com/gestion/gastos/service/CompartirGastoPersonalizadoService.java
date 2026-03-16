package com.gestion.gastos.service;

import com.gestion.gastos.model.dto.ApiOutResponseDto;
import com.gestion.gastos.model.dto.CompartirGastoPersonalizadoProjection;
import com.gestion.gastos.model.dto.CompartirGastoPersonalizadoRequest;
import com.gestion.gastos.model.dto.proyección.CardPersonalizadoResumen;
import com.gestion.gastos.model.dto.proyección.MovimientoPersonalizadoView;
import com.gestion.gastos.model.dto.proyección.ReporteMovimientoPersonalizadoView;
import com.gestion.gastos.model.entity.CardPersonalizadoEntity;
import com.gestion.gastos.model.entity.CompartirGastoPersonalizado;
import com.gestion.gastos.model.entity.Personas;
import com.gestion.gastos.model.entity.Usuario;
import com.gestion.gastos.repository.CardPersonalizadoRepository;
import com.gestion.gastos.repository.CompartirGastoPersonalizadoRepository;
import com.gestion.gastos.repository.MovimientoPersonalizadoRepository;
import com.gestion.gastos.repository.PersonaRepository;
import com.gestion.gastos.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompartirGastoPersonalizadoService {
    private static final int COD_OK = 1;
    private static final int COD_VALIDACION = 1001;
    private static final int COD_NO_ENCONTRADO = 1004;
    private static final int COD_US_NO_ENCONTRADO = 1003;
    private static final int COD_DUPLICADO = 1005;
    private static final int COD_ERROR = 1999;

    private final CompartirGastoPersonalizadoRepository compartirRepository;
    private final CardPersonalizadoRepository cardPersonalizadoRepository;
    private final PersonaRepository personaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoPersonalizadoRepository movimientoPersonalizadoRepository;
    private final AuthService authService;

    @Transactional
    public ApiOutResponseDto compartir(CompartirGastoPersonalizadoRequest req) {
        if (req == null) {
            return build(COD_VALIDACION, "Request es obligatorio", null, 0);
        }
        if (req.getCorreoDestinatario() == null || req.getCorreoDestinatario().isBlank()) {
            return build(COD_VALIDACION, "correoDestinatario es obligatorio", null, 0);
        }
        if (req.getIdGastoPersonalizado() == null) {
            return build(COD_VALIDACION, "idGastoPersonalizado es obligatorio", null, 0);
        }

        Usuario usuarioAutenticado = authService.getUsuarioAutenticado();
        Optional<Personas> personaAccionOpt =
                personaRepository.findByUsuarioId(Math.toIntExact(usuarioAutenticado.getId()));
        if (personaAccionOpt.isEmpty()) {
            return build(COD_NO_ENCONTRADO, "El usuario que comparte no tiene perfil de persona", null, 0);
        }

        Optional<CardPersonalizadoEntity> gastoOpt = cardPersonalizadoRepository
                .findByIdAndUserId(req.getIdGastoPersonalizado(), usuarioAutenticado.getId());
        if (gastoOpt.isEmpty()) {
            return build(COD_NO_ENCONTRADO, "El gasto personalizado no existe o no pertenece al usuario", null, 0);
        }

        Optional<Usuario> usuarioDestinoOpt = usuarioRepository.findByEmail(req.getCorreoDestinatario().trim());
        if (usuarioDestinoOpt.isEmpty()) {
            return build(COD_US_NO_ENCONTRADO, "No existe un usuario con el correo ingresado", null, 0);
        }

        Integer usuarioDestinoId = Math.toIntExact(usuarioDestinoOpt.get().getId());
        if (usuarioDestinoId.equals(Math.toIntExact(usuarioAutenticado.getId()))) {
            return build(COD_VALIDACION, "No puedes compartir a tu propio correo", null, 0);
        }

        Optional<Personas> personaDestinoOpt = personaRepository.findByUsuarioId(usuarioDestinoId);
        if (personaDestinoOpt.isEmpty()) {
            return build(COD_NO_ENCONTRADO, "El usuario destino no tiene perfil de persona", null, 0);
        }

        Personas personaAccion = personaAccionOpt.get();
        Personas personaDestino = personaDestinoOpt.get();

        boolean yaCompartido = compartirRepository
                .existsByIdPersonaCompartioAndIdPersonaCompartidaAndGastoPersonalizadoIdAndActivoTrue(
                        personaAccion.getId(),
                        personaDestino.getId(),
                        req.getIdGastoPersonalizado()
                );
        if (yaCompartido) {
            return build(COD_DUPLICADO, "El gasto personalizado ya fue compartido a esta persona", null, 0);
        }

        try {
            CompartirGastoPersonalizado guardado = compartirRepository.save(
                    CompartirGastoPersonalizado.builder()
                            .idPersonaCompartio(personaAccion.getId())
                            .idPersonaCompartida(personaDestino.getId())
                            .gastoPersonalizadoId(req.getIdGastoPersonalizado())
                            .activo(true)
                            .build()
            );
            return build(COD_OK, "Gasto personalizado compartido exitosamente", guardado, 1);
        } catch (Exception e) {
            return build(COD_ERROR, "Error al compartir gasto personalizado: " + e.getMessage(), null, 0);
        }
    }

    public ApiOutResponseDto listarRecibidos(Integer idUsuario) {
        Integer usuarioAutenticadoId = Math.toIntExact(authService.getUsuarioAutenticado().getId());
        if (idUsuario == null) {
            return build(COD_VALIDACION, "idUsuario es obligatorio", List.of(), 0);
        }
        if (!usuarioAutenticadoId.equals(idUsuario)) {
            return build(COD_VALIDACION, "No tienes permiso para ver comparticiones de otro usuario", List.of(), 0);
        }

        List<CompartirGastoPersonalizadoProjection> data = compartirRepository.listarRecibidos(idUsuario);
        return build(COD_OK, "Listado de gastos personalizados recibidos", data, data.size());
    }

    public ApiOutResponseDto listarEnviados(Integer idUsuario) {
        Integer usuarioAutenticadoId = Math.toIntExact(authService.getUsuarioAutenticado().getId());
        if (idUsuario == null) {
            return build(COD_VALIDACION, "idUsuario es obligatorio", List.of(), 0);
        }
        if (!usuarioAutenticadoId.equals(idUsuario)) {
            return build(COD_VALIDACION, "No tienes permiso para ver comparticiones de otro usuario", List.of(), 0);
        }

        List<CompartirGastoPersonalizadoProjection> data = compartirRepository.listarEnviados(idUsuario);
        return build(COD_OK, "Listado de gastos personalizados enviados", data, data.size());
    }

    @Transactional
    public ApiOutResponseDto desactivar(Integer idCompartir) {
        if (idCompartir == null) {
            return build(COD_VALIDACION, "id es obligatorio", null, 0);
        }

        Integer usuarioAutenticadoId = Math.toIntExact(authService.getUsuarioAutenticado().getId());
        Optional<Personas> personaOpt = personaRepository.findByUsuarioId(usuarioAutenticadoId);
        if (personaOpt.isEmpty()) {
            return build(COD_NO_ENCONTRADO, "El usuario autenticado no tiene perfil de persona", null, 0);
        }

        Optional<CompartirGastoPersonalizado> registroOpt = compartirRepository.findById(idCompartir);
        if (registroOpt.isEmpty()) {
            return build(COD_NO_ENCONTRADO, "No existe el registro de comparticion", null, 0);
        }

        if (!compartirRepository.existsByIdAndIdPersonaCompartioAndActivoTrue(idCompartir, personaOpt.get().getId())) {
            return build(COD_VALIDACION, "No tienes permiso para desactivar esta comparticion", null, 0);
        }

        try {
            CompartirGastoPersonalizado registro = registroOpt.get();
            registro.setActivo(false);
            compartirRepository.save(registro);
            return build(COD_OK, "Comparticion desactivada exitosamente", registro, 1);
        } catch (Exception e) {
            return build(COD_ERROR, "Error al desactivar comparticion: " + e.getMessage(), null, 0);
        }
    }

    public ApiOutResponseDto obtenerDetalleCompartido(Long idGastoPersonalizado) {
        if (!tieneAccesoAGasto(idGastoPersonalizado)) {
            return build(COD_VALIDACION, "No tienes permiso para ver este gasto personalizado", null, 0);
        }

        CardPersonalizadoResumen resumen =
                cardPersonalizadoRepository.CardPersonalizadosxIdSinValidacion(idGastoPersonalizado);
        if (resumen == null) {
            return build(COD_NO_ENCONTRADO, "No existe gasto personalizado", null, 0);
        }

        return build(COD_OK, "Detalle de gasto personalizado encontrado", resumen, 1);
    }

    public ApiOutResponseDto listarMovimientosCompartidos(Long idGastoPersonalizado) {
        if (!tieneAccesoAGasto(idGastoPersonalizado)) {
            return build(COD_VALIDACION, "No tienes permiso para ver este gasto personalizado", List.of(), 0);
        }

        List<MovimientoPersonalizadoView> data =
                movimientoPersonalizadoRepository.listMovimientoPersonalizado(idGastoPersonalizado);
        return build(COD_OK, "Movimientos encontrados", data, data.size());
    }

    public ApiOutResponseDto listarReporteCompartido(Long idGastoPersonalizado) {
        if (!tieneAccesoAGasto(idGastoPersonalizado)) {
            return build(COD_VALIDACION, "No tienes permiso para ver este gasto personalizado", List.of(), 0);
        }

        List<ReporteMovimientoPersonalizadoView> data =
                movimientoPersonalizadoRepository.listarReporteCard(idGastoPersonalizado);
        return build(COD_OK, "Reporte encontrado", data, data.size());
    }

    private ApiOutResponseDto build(Integer cod, String msg, Object data, Integer total) {
        ApiOutResponseDto out = new ApiOutResponseDto();
        out.setCodResultado(cod);
        out.setMsgResultado(msg);
        out.setResponse(data);
        out.setTotal(total);
        return out;
    }

    private boolean tieneAccesoAGasto(Long idGastoPersonalizado) {
        Usuario usuarioAutenticado = authService.getUsuarioAutenticado();

        if (cardPersonalizadoRepository.findByIdAndUserId(idGastoPersonalizado, usuarioAutenticado.getId()).isPresent()) {
            return true;
        }

        return personaRepository.findByUsuarioId(Math.toIntExact(usuarioAutenticado.getId()))
                .map(persona -> compartirRepository
                        .existsByIdPersonaCompartidaAndGastoPersonalizadoIdAndActivoTrue(
                                persona.getId(),
                                idGastoPersonalizado
                        ))
                .orElse(false);
    }
}
