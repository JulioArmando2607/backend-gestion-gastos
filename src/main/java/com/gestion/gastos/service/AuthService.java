package com.gestion.gastos.service;

import com.gestion.gastos.model.dto.LoginRequest;
import com.gestion.gastos.model.dto.RecoveryLookupRequest;
import com.gestion.gastos.model.dto.RecoveryLookupResponse;
import com.gestion.gastos.model.dto.RecoveryResetPasswordRequest;
import com.gestion.gastos.model.dto.RecoveryVerifyAnswerRequest;
import com.gestion.gastos.model.entity.Personas;
import com.gestion.gastos.model.entity.Usuario;
import com.gestion.gastos.repository.PersonaRepository;
import com.gestion.gastos.repository.UsuarioRepository;
import com.gestion.gastos.security.JwtService;
import com.gestion.gastos.security.UsuarioAutenticadoService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;

@Component
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final JwtService jwtService;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            PersonaRepository personaRepository,
            JwtService jwtService,
            UsuarioAutenticadoService usuarioAutenticadoService,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.personaRepository = personaRepository;
        this.jwtService = jwtService;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return jwtService.generateToken(usuario);
    }

    public Usuario getUsuarioAutenticado() {
        return usuarioAutenticadoService.obtenerUsuario();
    }

    public RecoveryLookupResponse lookupRecoveryAccount(RecoveryLookupRequest request) {
        Usuario usuario = findUsuarioByIdentifier(request.getIdentifier())
                .orElseThrow(() -> new UsernameNotFoundException("Cuenta no encontrada"));

        Personas persona = personaRepository.findByUsuarioId(Math.toIntExact(usuario.getId()))
                .orElse(null);

        boolean hasRecoveryData = hasRecoveryData(usuario, persona);

        return RecoveryLookupResponse.builder()
                .accountFound(true)
                .hasRecoveryData(hasRecoveryData)
                .preguntaRecuperacion(hasRecoveryData ? persona.getPreguntaRecuperacion() : null)
                .message(hasRecoveryData
                        ? "Cuenta validada correctamente"
                        : "La cuenta no tiene datos de recuperación configurados")
                .build();
    }

    public boolean verifyRecoveryAnswer(RecoveryVerifyAnswerRequest request) {
        Usuario usuario = findUsuarioByIdentifier(request.getIdentifier())
                .orElseThrow(() -> new UsernameNotFoundException("Cuenta no encontrada"));

        Personas persona = personaRepository.findByUsuarioId(Math.toIntExact(usuario.getId()))
                .orElseThrow(() -> new IllegalStateException("La cuenta no tiene datos de recuperación configurados"));

        if (!hasRecoveryData(usuario, persona)) {
            throw new IllegalStateException("La cuenta no tiene datos de recuperación configurados");
        }

        return secureEquals(normalizeText(persona.getRespuestaRecuperacion()),
                normalizeText(request.getRespuestaRecuperacion()));
    }

    public void resetPasswordByRecovery(RecoveryResetPasswordRequest request) {
        if (request.getNewPassword() == null || request.getNewPassword().trim().length() < 6) {
            throw new IllegalArgumentException("La nueva clave debe tener al menos 6 caracteres");
        }

        Usuario usuario = findUsuarioByIdentifier(request.getIdentifier())
                .orElseThrow(() -> new UsernameNotFoundException("Cuenta no encontrada"));

        Personas persona = personaRepository.findByUsuarioId(Math.toIntExact(usuario.getId()))
                .orElseThrow(() -> new IllegalStateException("La cuenta no tiene datos de recuperación configurados"));

        if (!hasRecoveryData(usuario, persona)) {
            throw new IllegalStateException("La cuenta no tiene datos de recuperación configurados");
        }

        boolean validAnswer = secureEquals(
                normalizeText(persona.getRespuestaRecuperacion()),
                normalizeText(request.getRespuestaRecuperacion())
        );

        if (!validAnswer) {
            throw new IllegalArgumentException("La respuesta secreta no coincide");
        }

        usuario.setPassword(passwordEncoder.encode(request.getNewPassword().trim()));
        usuarioRepository.save(usuario);
    }

    private Optional<Usuario> findUsuarioByIdentifier(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return Optional.empty();
        }

        String normalized = identifier.trim();
        Optional<Usuario> byEmail = usuarioRepository.findByEmail(normalized);
        if (byEmail.isPresent()) {
            return byEmail;
        }

        return usuarioRepository.findByNombreIgnoreCase(normalized);
    }

    private boolean hasRecoveryData(Usuario usuario, Personas persona) {
        return usuario != null
                && persona != null
                && notBlank(usuario.getEmail())
                && notBlank(persona.getCelular())
                && persona.getFechaNacimiento() != null
                && notBlank(persona.getPreguntaRecuperacion())
                && notBlank(persona.getRespuestaRecuperacion());
    }

    private boolean notBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String normalizeText(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private boolean secureEquals(String left, String right) {
        return MessageDigest.isEqual(
                left.getBytes(StandardCharsets.UTF_8),
                right.getBytes(StandardCharsets.UTF_8)
        );
    }
}
