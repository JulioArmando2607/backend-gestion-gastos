package com.gestion.gastos.security;

import com.gestion.gastos.model.entity.Usuario;
import com.gestion.gastos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioAutenticadoService {

    private final UsuarioRepository usuarioRepository;

    public Long obtenerUsuarioId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UsuarioAutenticado usuarioAutenticado) {
            return usuarioAutenticado.getId();
        }

        throw new AccessDeniedException("No se pudo obtener el usuario autenticado");
    }

    public Integer obtenerUsuarioIdComoInteger() {
        return Math.toIntExact(obtenerUsuarioId());
    }

    public Usuario obtenerUsuario() {
        return usuarioRepository.findById(obtenerUsuarioId())
                .orElseThrow(() -> new AccessDeniedException("Usuario autenticado no encontrado"));
    }
}
