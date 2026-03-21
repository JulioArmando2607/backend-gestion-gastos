package com.gestion.gastos.service;

import com.gestion.gastos.model.dto.ApiOutResponseDto;
import com.gestion.gastos.model.dto.CardPersonalizadoResponse;
import com.gestion.gastos.model.dto.CategoriaPersonalizadoRequest;
import com.gestion.gastos.model.dto.CrearCardPersonalizadoRequest;
import com.gestion.gastos.model.dto.EditarCardPersonalizadoRequest;
import com.gestion.gastos.model.dto.MovimientoPersonalizado;
import com.gestion.gastos.model.dto.proyección.CardPersonalizadoResumen;
import com.gestion.gastos.model.dto.proyección.CategoriaPersonalizadoProjection;
import com.gestion.gastos.model.dto.proyección.MovimientoPersonalizadoView;
import com.gestion.gastos.model.dto.proyección.ReporteMovimientoPersonalizadoView;
import com.gestion.gastos.model.entity.CardPersonalizadoEntity;
import com.gestion.gastos.model.entity.CategoriaPersonalizadoEntity;
import com.gestion.gastos.model.entity.MovimientoPersonalizadoEntity;
import com.gestion.gastos.model.entity.Usuario;
import com.gestion.gastos.repository.CardPersonalizadoRepository;
import com.gestion.gastos.repository.CompartirGastoPersonalizadoRepository;
import com.gestion.gastos.repository.CategoriaPersonalizadoRepository;
import com.gestion.gastos.repository.MovimientoPersonalizadoRepository;
import com.gestion.gastos.repository.PersonaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GastosPersonalizadosService {
    private final CardPersonalizadoRepository cardPersonalizadoRepository;
    private final CategoriaPersonalizadoRepository categoriaPersonalizadoRepository;
    private final MovimientoPersonalizadoRepository movimientoPersonalizadoRepository;
    private final CompartirGastoPersonalizadoRepository compartirGastoPersonalizadoRepository;
    private final PersonaRepository personaRepository;
    private final GastoPersonalizadoRealtimeNotifier realtimeNotifier;
    private final AuthService authService;

    public List<CardPersonalizadoResumen> listarCardsPorUsuario() {
        Usuario usuario = authService.getUsuarioAutenticado();
        return cardPersonalizadoRepository.listarResumenPorUsuario(usuario.getId());
    }

    public CardPersonalizadoResponse crearGastoPersonalizado(CrearCardPersonalizadoRequest req) {
        String nombre = req.getNombre().trim();
        Usuario usuario = authService.getUsuarioAutenticado();

        if (cardPersonalizadoRepository.existsByUserIdAndNombreIgnoreCase(usuario.getId(), nombre)) {
            throw new IllegalArgumentException("Ya existe un card con ese nombre para el usuario.");
        }

        CardPersonalizadoEntity entity = CardPersonalizadoEntity.builder()
                .userId(usuario.getId())
                .nombre(nombre)
                .descripcion(req.getDescripcion())
                .moneda(Optional.ofNullable(req.getMoneda()).orElse("PEN").toUpperCase())
                .colorHex(Optional.ofNullable(req.getColorHex()).orElse("#6C63FF"))
                .archivado(false)
                .build();

        CardPersonalizadoEntity saved = cardPersonalizadoRepository.save(entity);
        _crearCategoriasIniciales(saved, usuario);
        return CardPersonalizadoResponse.builder()
                .id(saved.getId())
                .userId(saved.getUserId())
                .nombre(saved.getNombre())
                .descripcion(saved.getDescripcion())
                .moneda(saved.getMoneda())
                .colorHex(saved.getColorHex())
                .icono(saved.getIcono())
                .archivado(Boolean.TRUE.equals(saved.getArchivado()))
                .createdAt(saved.getCreatedAt())
                .build();
    }

    private void _crearCategoriasIniciales(CardPersonalizadoEntity card, Usuario usuario) {
        final List<CategoriaPersonalizadoEntity> categorias = List.of(
                _nuevaCategoriaBase(card, usuario, "Comida", "GASTO", 1),
                _nuevaCategoriaBase(card, usuario, "Transporte", "GASTO", 2),
                _nuevaCategoriaBase(card, usuario, "Casa", "GASTO", 3),
                _nuevaCategoriaBase(card, usuario, "Salud", "GASTO", 4),
                _nuevaCategoriaBase(card, usuario, "Otros gastos", "GASTO", 5),
                _nuevaCategoriaBase(card, usuario, "Sueldo", "INGRESO", 6),
                _nuevaCategoriaBase(card, usuario, "Ingreso extra", "INGRESO", 7)
        );
        categoriaPersonalizadoRepository.saveAll(categorias);
    }

    private CategoriaPersonalizadoEntity _nuevaCategoriaBase(
            CardPersonalizadoEntity card,
            Usuario usuario,
            String nombre,
            String tipo,
            int orden
    ) {
        return CategoriaPersonalizadoEntity.builder()
                .userId(usuario.getId())
                .cardId(card.getId())
                .nombre(nombre)
                .tipo(tipo)
                .orden(orden)
                .activa(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public CategoriaPersonalizadoEntity crearCategoria(CategoriaPersonalizadoRequest categoria) {
        if (!canEditCard(Long.valueOf(categoria.getIdCard()))) {
            throw new IllegalArgumentException("No tienes permiso para editar esta cuenta");
        }
        Usuario usuario = authService.getUsuarioAutenticado();
        cardPersonalizadoRepository.getReferenceById(Long.valueOf(categoria.getIdCard()));

        CategoriaPersonalizadoEntity entity = CategoriaPersonalizadoEntity.builder()
                .userId(usuario.getId())
                .cardId(Long.valueOf(categoria.getIdCard()))
                .nombre(categoria.getNombre())
                .tipo(categoria.getTipoMovimiento())
                .createdAt(LocalDateTime.now())
                .activa(true)
                .build();

        CategoriaPersonalizadoEntity saved = categoriaPersonalizadoRepository.save(entity);
        realtimeNotifier.notifyChange("categoria_personalizada_creada", saved.getCardId(), usuario.getId());
        return saved;
    }

    public List<CategoriaPersonalizadoProjection> listarCategoria(Integer idCard) {
        if (!canViewCard(Long.valueOf(idCard))) {
            return List.of();
        }
        Usuario usuario = authService.getUsuarioAutenticado();
        return cardPersonalizadoRepository.listCategoriaPersonalizado(usuario.getId(), idCard);
    }

    public List<CategoriaPersonalizadoProjection> listCategoriaPersonalizadoxTipo(Integer idCard, String tipo) {
        if (!canViewCard(Long.valueOf(idCard))) {
            return List.of();
        }
        Usuario usuario = authService.getUsuarioAutenticado();
        return cardPersonalizadoRepository.listCategoriaPersonalizadoxTipo(usuario.getId(), idCard, tipo);
    }

    public CardPersonalizadoResumen CardPersonalizadosxId(Integer idCard) {
        if (!canViewCard(Long.valueOf(idCard))) {
            return null;
        }
        Usuario usuario = authService.getUsuarioAutenticado();
        return cardPersonalizadoRepository.CardPersonalizadosxId(Math.toIntExact(usuario.getId()), idCard);
    }

    public List<MovimientoPersonalizadoView> listMovimientoPersonalizado(Integer idCard) {
        if (!canViewCard(Long.valueOf(idCard))) {
            return List.of();
        }
        return movimientoPersonalizadoRepository.listMovimientoPersonalizado(Long.valueOf(idCard));
    }

    public List<ReporteMovimientoPersonalizadoView> listarReporteCard(Integer idCard) {
        if (!canViewCard(Long.valueOf(idCard))) {
            return List.of();
        }
        return movimientoPersonalizadoRepository.listarReporteCard(Long.valueOf(idCard));
    }

    @Transactional
    public ApiOutResponseDto nuevoGasto(MovimientoPersonalizado dto) {
        ApiOutResponseDto out = new ApiOutResponseDto();
        Usuario usuario = authService.getUsuarioAutenticado();
        if (!canEditCard(dto.getIdCard())) {
            out.setCodResultado(1001);
            out.setMsgResultado("No tienes permiso para editar esta cuenta");
            out.setResponse(null);
            return out;
        }

        CardPersonalizadoEntity cardRef = cardPersonalizadoRepository.getReferenceById(dto.getIdCard());
        CategoriaPersonalizadoEntity catRef = categoriaPersonalizadoRepository
                .findByIdAndCardId(dto.getCategoria(), dto.getIdCard())
                .orElseThrow(() -> new IllegalArgumentException(
                        "La categoria " + dto.getCategoria() + " no pertenece a la card " + dto.getIdCard()
                ));

        var tipoEnum = CategoriaPersonalizadoEntity.TipoMovimiento.valueOf(dto.getTipo().toUpperCase());
        MovimientoPersonalizadoEntity entity;
        String eventType;

        if (dto.getIdMovimiento() != null && dto.getIdMovimiento() > 0) {
            entity = movimientoPersonalizadoRepository.findById(dto.getIdMovimiento())
                    .orElseThrow(() -> new IllegalArgumentException("Movimiento no encontrado"));

            entity.setCategoria(catRef);
            entity.setTipo(tipoEnum);
            entity.setMonto(dto.getMonto() instanceof BigDecimal
                    ? (BigDecimal) dto.getMonto()
                    : new BigDecimal(dto.getMonto().toString()));
            entity.setFecha(dto.getFecha());
            entity.setNota(dto.getDescripcion());
            entity.setUpdatedAt(LocalDateTime.now());

            movimientoPersonalizadoRepository.save(entity);
            out.setMsgResultado("Actualizado correctamente");
            eventType = "movimiento_personalizado_editado";
        } else {
            entity = MovimientoPersonalizadoEntity.builder()
                    .card(cardRef)
                    .categoria(catRef)
                    .tipo(tipoEnum)
                    .monto(dto.getMonto() instanceof BigDecimal
                            ? (BigDecimal) dto.getMonto()
                            : new BigDecimal(dto.getMonto().toString()))
                    .fecha(dto.getFecha())
                    .nota(dto.getDescripcion())
                    .createdAt(LocalDateTime.now())
                    .activo(true)
                    .build();

            movimientoPersonalizadoRepository.save(entity);
            out.setMsgResultado("Registrado correctamente");
            eventType = "movimiento_personalizado_creado";
        }

        realtimeNotifier.notifyChange(eventType, dto.getIdCard(), usuario.getId());
        out.setCodResultado(200);
        return out;
    }

    public void eliminarMoviento(Long id) {
        Usuario usuario = authService.getUsuarioAutenticado();
        movimientoPersonalizadoRepository.findById(id).ifPresent(mov -> {
            if (!canEditCard(mov.getCard().getId())) {
                return;
            }
            mov.setActivo(false);
            movimientoPersonalizadoRepository.save(mov);
            realtimeNotifier.notifyChange("movimiento_personalizado_eliminado", mov.getCard().getId(), usuario.getId());
        });
    }

    public MovimientoPersonalizadoView obtenerMovimientoPersonalizado(Long idMovimiento) {
        Optional<MovimientoPersonalizadoEntity> movimientoEntity = movimientoPersonalizadoRepository.findById(idMovimiento);
        if (movimientoEntity.isEmpty() || !canViewCard(movimientoEntity.get().getCard().getId())) {
            return null;
        }
        return movimientoPersonalizadoRepository.obtenerMovimientoPersonalizado(idMovimiento);
    }

    public void eliminarCategoria(Long id) {
        Usuario usuario = authService.getUsuarioAutenticado();
        categoriaPersonalizadoRepository.findById(id).ifPresent(cat -> {
            if (!canEditCard(cat.getCardId())) {
                return;
            }
            cat.setActiva(false);
            categoriaPersonalizadoRepository.save(cat);
            realtimeNotifier.notifyChange("categoria_personalizada_eliminada", cat.getCardId(), usuario.getId());
        });
    }

    @Transactional
    public ApiOutResponseDto editarCard(Long idCard, EditarCardPersonalizadoRequest req) {
        ApiOutResponseDto out = new ApiOutResponseDto();

        if (idCard == null || idCard <= 0) {
            out.setCodResultado(1001);
            out.setMsgResultado("idCard es obligatorio");
            out.setResponse(null);
            return out;
        }
        if (req == null) {
            out.setCodResultado(1001);
            out.setMsgResultado("Request es obligatorio");
            out.setResponse(null);
            return out;
        }

        Usuario usuario = authService.getUsuarioAutenticado();
        Optional<CardPersonalizadoEntity> cardOpt = cardPersonalizadoRepository.findByIdAndUserId(idCard, usuario.getId());
        if (cardOpt.isEmpty()) {
            out.setCodResultado(1004);
            out.setMsgResultado("No existe card para el usuario autenticado");
            out.setResponse(null);
            return out;
        }

        CardPersonalizadoEntity card = cardOpt.get();
        if (Boolean.TRUE.equals(card.getArchivado())) {
            out.setCodResultado(1001);
            out.setMsgResultado("No se puede editar una card archivada");
            out.setResponse(null);
            return out;
        }

        if (req.getNombre() != null) {
            String nombre = req.getNombre().trim();
            if (nombre.isEmpty()) {
                out.setCodResultado(1001);
                out.setMsgResultado("nombre no puede ser vacio");
                out.setResponse(null);
                return out;
            }

            boolean existeOtro = cardPersonalizadoRepository
                    .existsByUserIdAndNombreIgnoreCaseAndIdNot(usuario.getId(), nombre, idCard);
            if (existeOtro) {
                out.setCodResultado(1005);
                out.setMsgResultado("Ya existe otra card con ese nombre para el usuario");
                out.setResponse(null);
                return out;
            }
            card.setNombre(nombre);
        }

        if (req.getDescripcion() != null) {
            card.setDescripcion(req.getDescripcion());
        }
        if (req.getMoneda() != null && !req.getMoneda().isBlank()) {
            card.setMoneda(req.getMoneda().toUpperCase());
        }
        if (req.getColorHex() != null && !req.getColorHex().isBlank()) {
            card.setColorHex(req.getColorHex());
        }

        CardPersonalizadoEntity saved = cardPersonalizadoRepository.save(card);
        realtimeNotifier.notifyChange("gasto_personalizado_editado", saved.getId(), usuario.getId());
        CardPersonalizadoResponse response = CardPersonalizadoResponse.builder()
                .id(saved.getId())
                .userId(saved.getUserId())
                .nombre(saved.getNombre())
                .descripcion(saved.getDescripcion())
                .moneda(saved.getMoneda())
                .colorHex(saved.getColorHex())
                .icono(saved.getIcono())
                .archivado(Boolean.TRUE.equals(saved.getArchivado()))
                .createdAt(saved.getCreatedAt())
                .build();

        out.setCodResultado(1);
        out.setMsgResultado("Card actualizada correctamente");
        out.setResponse(response);
        return out;
    }

    @Transactional
    public ApiOutResponseDto eliminarCard(Long idCard) {
        ApiOutResponseDto out = new ApiOutResponseDto();

        if (idCard == null || idCard <= 0) {
            out.setCodResultado(1001);
            out.setMsgResultado("idCard es obligatorio");
            out.setResponse(null);
            return out;
        }

        Usuario usuario = authService.getUsuarioAutenticado();
        Optional<CardPersonalizadoEntity> cardOpt = cardPersonalizadoRepository
                .findByIdAndUserId(idCard, usuario.getId());

        if (cardOpt.isEmpty()) {
            out.setCodResultado(1004);
            out.setMsgResultado("No existe card para el usuario autenticado");
            out.setResponse(null);
            return out;
        }

        CardPersonalizadoEntity card = cardOpt.get();
        if (Boolean.TRUE.equals(card.getArchivado())) {
            out.setCodResultado(1);
            out.setMsgResultado("La card ya estaba archivada");
            out.setResponse(card.getId());
            return out;
        }

        card.setArchivado(true);
        cardPersonalizadoRepository.save(card);
        realtimeNotifier.notifyChange("gasto_personalizado_eliminado", card.getId(), usuario.getId());

        out.setCodResultado(1);
        out.setMsgResultado("Card archivada correctamente");
        out.setResponse(card.getId());
        return out;
    }

    private boolean canViewCard(Long idCard) {
        Usuario usuario = authService.getUsuarioAutenticado();
        if (cardPersonalizadoRepository.findByIdAndUserId(idCard, usuario.getId()).isPresent()) {
            return true;
        }

        return personaRepository.findByUsuarioId(Math.toIntExact(usuario.getId()))
                .map(persona -> compartirGastoPersonalizadoRepository
                        .existsByIdPersonaCompartidaAndGastoPersonalizadoIdAndActivoTrue(
                                persona.getId(),
                                idCard
                        ))
                .orElse(false);
    }

    private boolean canEditCard(Long idCard) {
        Usuario usuario = authService.getUsuarioAutenticado();
        if (cardPersonalizadoRepository.findByIdAndUserId(idCard, usuario.getId()).isPresent()) {
            return true;
        }

        return personaRepository.findByUsuarioId(Math.toIntExact(usuario.getId()))
                .map(persona -> compartirGastoPersonalizadoRepository
                        .countAccessByPermiso(persona.getId(), idCard, "EDITAR") > 0)
                .orElse(false);
    }
}
