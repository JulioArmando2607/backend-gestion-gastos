package com.gestion.gastos.service;

import com.gestion.gastos.model.dto.PersonaUsuarioDto;
import com.gestion.gastos.model.entity.Personas;
import com.gestion.gastos.model.entity.Usuario;
import com.gestion.gastos.repository.PersonaRepository;
import com.gestion.gastos.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;


    public Usuario guardar(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        Personas nuevaPersona = Personas.builder()
                .activo(true)
                .fechaReg(LocalDateTime.now())
                .usuarioId(Math.toIntExact(usuarioGuardado.getId()))
                .build();

        personaRepository.save(nuevaPersona);
        return usuarioGuardado;
        /*  int id = usuarioRepository.save(usuario);
        Personas guardada;


            Personas nueva = Personas.builder()

                    .activo(true)
                    .fechaReg(LocalDateTime.now())
                    .usuarioId(id)
                    .build();
            guardada = personaRepository.save(nueva);
        return usuarioRepository.save(usuario);*/
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Personas> buscarPersonaPorUsuarioId(Long usuarioId) {
        return personaRepository.findByUsuarioId(Math.toIntExact(usuarioId));
    }

    public Optional<PersonaUsuarioDto> buscarPersonaConUsuario(Long usuarioId) {
        Optional<Personas> personaOpt = personaRepository.findByUsuarioId(Math.toIntExact(usuarioId));
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);

        if (personaOpt.isEmpty() || usuarioOpt.isEmpty()) {
            return Optional.empty();
        }

        Personas persona = personaOpt.get();
        Usuario usuario = usuarioOpt.get();

        return Optional.of(
                PersonaUsuarioDto.builder()
                        .id(persona.getId())
                        .celular(persona.getCelular())
                        .fechaNacimiento(persona.getFechaNacimiento())
                        .preguntaRecuperacion(persona.getPreguntaRecuperacion())
                        .respuestaRecuperacion(persona.getRespuestaRecuperacion())
                        .activo(persona.getActivo())
                        .fechaReg(persona.getFechaReg())
                        .usuarioId(persona.getUsuarioId())
                        .nombre(usuario.getNombre())
                        .email(usuario.getEmail())
                        .build()
        );
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

    public boolean mostrarBoton(String codigoBoton) {
        if(usuarioRepository.mostrarBoton(codigoBoton).equals(1)){
         return true;
        }else{
            return false;
        }
    }

    public PersonaUsuarioDto actualizarPersona(PersonaUsuarioDto personas) {

        Integer id = personas.getId();
        Personas guardada;

        if (id == null || id == 0) {
            Personas nueva = Personas.builder()
                    .celular(personas.getCelular())
                    .fechaNacimiento(personas.getFechaNacimiento())
                    .preguntaRecuperacion(personas.getPreguntaRecuperacion())
                    .respuestaRecuperacion(personas.getRespuestaRecuperacion())
                    .activo(personas.getActivo() != null ? personas.getActivo() : true)
                    .fechaReg(personas.getFechaReg() != null ? personas.getFechaReg() : LocalDateTime.now())
                    .usuarioId(personas.getUsuarioId())
                    .build();
            guardada = personaRepository.save(nueva);
        } else {
            guardada = personaRepository.findById(id).map(existente -> {
                existente.setCelular(personas.getCelular());
                existente.setFechaNacimiento(personas.getFechaNacimiento());
                existente.setPreguntaRecuperacion(personas.getPreguntaRecuperacion());
                existente.setRespuestaRecuperacion(personas.getRespuestaRecuperacion());
                existente.setActivo(personas.getActivo());
                existente.setUsuarioId(personas.getUsuarioId());
                return personaRepository.save(existente);
            }).orElseGet(() -> {
                Personas nueva = Personas.builder()
                        .celular(personas.getCelular())
                        .fechaNacimiento(personas.getFechaNacimiento())
                        .preguntaRecuperacion(personas.getPreguntaRecuperacion())
                        .respuestaRecuperacion(personas.getRespuestaRecuperacion())
                        .activo(personas.getActivo() != null ? personas.getActivo() : true)
                        .fechaReg(personas.getFechaReg() != null ? personas.getFechaReg() : LocalDateTime.now())
                        .usuarioId(personas.getUsuarioId())
                        .build();
                return personaRepository.save(nueva);
            });
        }

        if (personas.getUsuarioId() != null) {
            Usuario existenteU = usuarioRepository.findById(Long.valueOf(personas.getUsuarioId()))
                    .orElseThrow(() -> new EntityNotFoundException("Usuario " + personas.getUsuarioId() + " no existe"));

            existenteU.setNombre(personas.getNombre());
            existenteU.setEmail(personas.getEmail());
            usuarioRepository.save(existenteU);
        }

        return PersonaUsuarioDto.builder()
                .id(guardada.getId())
                .celular(guardada.getCelular())
                .fechaNacimiento(guardada.getFechaNacimiento())
                .preguntaRecuperacion(guardada.getPreguntaRecuperacion())
                .respuestaRecuperacion(guardada.getRespuestaRecuperacion())
                .activo(guardada.getActivo())
                .fechaReg(guardada.getFechaReg())
                .usuarioId(guardada.getUsuarioId())
                .nombre(personas.getNombre())
                .email(personas.getEmail())
                .build();
    }
}
