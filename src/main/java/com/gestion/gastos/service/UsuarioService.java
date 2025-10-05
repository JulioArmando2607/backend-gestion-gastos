package com.gestion.gastos.service;

import com.gestion.gastos.model.entity.Usuario;
import com.gestion.gastos.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    public Usuario guardar(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario editar(Long id, Usuario usuario) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario " + id + " no existe"));

        // Reemplazo total (PUT): copiar todos los campos
        existente.setNombre(usuario.getNombre());
        existente.setEmail(usuario.getEmail());

        return usuarioRepository.save(existente); // actualiza, nunca inserta porque ya existe
    }
}
