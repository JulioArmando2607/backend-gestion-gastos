package com.gestion.gastos.security;

import com.gestion.gastos.model.entity.Usuario;
import com.gestion.gastos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String idOrEmail) throws UsernameNotFoundException {
        Usuario usuario;
        try {
            Long id = Long.parseLong(idOrEmail);
            usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con id: " + id));
        } catch (NumberFormatException ex) {
            usuario = usuarioRepository.findByEmail(idOrEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + idOrEmail));
        }

        return new UsuarioAutenticado(
                usuario,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
