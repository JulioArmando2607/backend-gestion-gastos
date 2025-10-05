package com.gestion.gastos.security;

import com.gestion.gastos.model.entity.Usuario;
import com.gestion.gastos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String idOrEmail) throws UsernameNotFoundException {
        // En nuestro diseño, viene un id en String
        Usuario usuario;
        try {
            Long id = Long.parseLong(idOrEmail);
            usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con id: " + id));
        } catch (NumberFormatException ex) {
            // (Opcional) permitir lookup por email si alguna vez pasas un email
            usuario = usuarioRepository.findByEmail(idOrEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + idOrEmail));
        }

        return User.builder()
                .username(usuario.getEmail())      // username visible en Spring Security
                .password(usuario.getPassword())
                .roles("USER")                     // ajusta según tu modelo
                .build();
    }
}
