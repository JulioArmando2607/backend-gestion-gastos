package com.gestion.gastos.service;

import com.gestion.gastos.model.dto.*;
import com.gestion.gastos.model.dto.proyección.CardPersonalizadoResumen;
import com.gestion.gastos.model.dto.proyección.CategoriaPersonalizadoProjection;
import com.gestion.gastos.model.dto.proyección.MovimientoPersonalizadoView;
import com.gestion.gastos.model.entity.*;
import com.gestion.gastos.repository.CardPersonalizadoRepository;
import com.gestion.gastos.repository.CategoriaPersonalizadoRepository;
import com.gestion.gastos.repository.MovimientoPersonalizadoRepository;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GastosPersonalizadosService {
    private final CardPersonalizadoRepository cardPersonalizadoRepository;
    private final CategoriaPersonalizadoRepository categoriaPersonalizadoRepository;
    private final MovimientoPersonalizadoRepository movimientoPersonalizadoRepository;
    private final AuthService authService;

    public List<CardPersonalizadoResumen> listarCardsPorUsuario() {
        Usuario usuario = authService.getUsuarioAutenticado();

        return cardPersonalizadoRepository.listarResumenPorUsuario(usuario.getId());

    }

    public CardPersonalizadoResponse crearGastoPersonalizado(CrearCardPersonalizadoRequest req) {
        // normalizar
        String nombre = req.getNombre().trim();
        Usuario usuario = authService.getUsuarioAutenticado();

        // unicidad por usuario
        if (cardPersonalizadoRepository.existsByUserIdAndNombreIgnoreCase(usuario.getId(), nombre)) {
            throw new IllegalArgumentException("Ya existe un card con ese nombre para el usuario.");
        }

        // construir entidad
        CardPersonalizadoEntity entity = CardPersonalizadoEntity.builder()
                .userId(usuario.getId())
                .nombre(nombre)
                .descripcion(req.getDescripcion())
                .moneda(Optional.ofNullable(req.getMoneda()).orElse("PEN").toUpperCase())
                .colorHex(Optional.ofNullable(req.getColorHex()).orElse("#6C63FF"))
             //   .icono(Optional.ofNullable(req.getIcono()).orElse("settings"))
                .archivado(false)
                .build();

        CardPersonalizadoEntity saved = cardPersonalizadoRepository.save(entity);

        // map a response
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

    public CategoriaPersonalizadoEntity crearCategoria(CategoriaPersonalizadoRequest categoria) {
        // normalizar
        System.out.println(categoria.getIdCard());
        String nombre = categoria.getNombre().trim();
        Usuario usuario = authService.getUsuarioAutenticado();
        /*
        // unicidad por usuario
        if (categoriaPersonalizadoRepository.existsByUserIdAndNombreIgnoreCase(usuario.getId(), nombre)) {
            throw new IllegalArgumentException("Ya existe un card con ese nombre para el usuario.");
        }*/

        CardPersonalizadoEntity cardRef =
                cardPersonalizadoRepository.getReferenceById(Long.valueOf(categoria.getIdCard())); // proxy, sin SELECT

        CategoriaPersonalizadoEntity entity = CategoriaPersonalizadoEntity.builder()
                .userId(usuario.getId())
                .cardId(Long.valueOf(categoria.getIdCard()))                              // << aquí va la referencia
                .nombre(categoria.getNombre())
                .tipo(categoria.getTipoMovimiento())
                .createdAt(LocalDateTime.now())            // o usa @CreationTimestamp
                .activa(true)
                .build();

        return categoriaPersonalizadoRepository.save(entity);

    }

    public List<CategoriaPersonalizadoProjection> listarCategoria(Integer idCard) {
        Usuario usuario = authService.getUsuarioAutenticado();

        return cardPersonalizadoRepository.listCategoriaPersonalizado(usuario.getId(), idCard);
    }
    public List<CategoriaPersonalizadoProjection> listCategoriaPersonalizadoxTipo(Integer idCard,String tipo) {
        Usuario usuario = authService.getUsuarioAutenticado();

        return cardPersonalizadoRepository.listCategoriaPersonalizadoxTipo(usuario.getId(), idCard,tipo);
    }

    public CardPersonalizadoResumen CardPersonalizadosxId(Integer idCard) {
        Usuario usuario = authService.getUsuarioAutenticado();

        return cardPersonalizadoRepository.CardPersonalizadosxId(Math.toIntExact(usuario.getId()),idCard);

    }


    public List<MovimientoPersonalizadoView> listMovimientoPersonalizado(Integer idCard) {
        return  movimientoPersonalizadoRepository.listMovimientoPersonalizado(Long.valueOf(idCard));
    }

    public ApiOutResponseDto nuevoGasto(MovimientoPersonalizado movimientoPersonalizado) {
        ApiOutResponseDto apiOutResponseDto = new ApiOutResponseDto();
        CategoriaPersonalizadoEntity catref =
                categoriaPersonalizadoRepository.getReferenceById(Long.valueOf(movimientoPersonalizado.getIdCard())); // proxy, sin SELECT
        CardPersonalizadoEntity cardRef =
                cardPersonalizadoRepository.getReferenceById(Long.valueOf(movimientoPersonalizado.getIdCard())); // proxy, sin SELECT

        MovimientoPersonalizadoEntity entity = MovimientoPersonalizadoEntity.builder()
                .card(cardRef)
                .categoria(catref)
                .tipo(CategoriaPersonalizadoEntity.TipoMovimiento.valueOf(movimientoPersonalizado.getTipo()))                              // << aquí va la referencia
                .monto(movimientoPersonalizado.getMonto() )
                .fecha(movimientoPersonalizado.getFecha())
                .nota(movimientoPersonalizado.getDescripcion())
                .createdAt(LocalDateTime.now())            // o usa @CreationTimestamp
                .activo(true)
                .build();
        movimientoPersonalizadoRepository.save(entity);
        apiOutResponseDto.setCodResultado(200);
        apiOutResponseDto.setMsgResultado("Registrado");
        return apiOutResponseDto;

    }
}
