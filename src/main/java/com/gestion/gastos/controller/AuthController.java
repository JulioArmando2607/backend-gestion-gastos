package com.gestion.gastos.controller;

import com.gestion.gastos.model.dto.LoginRequest;
import com.gestion.gastos.model.dto.LoginResponse;
import com.gestion.gastos.model.dto.RecoveryLookupRequest;
import com.gestion.gastos.model.dto.RecoveryLookupResponse;
import com.gestion.gastos.model.dto.RecoveryResetPasswordRequest;
import com.gestion.gastos.model.dto.RecoveryVerifyAnswerRequest;
import com.gestion.gastos.model.entity.Usuario;
import com.gestion.gastos.service.AuthService;
import com.gestion.gastos.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @GetMapping("/estado")
    public boolean estado() {
        System.out.println("aqui esty");
         return true;
    }
    @PostMapping("/register")
    public ResponseEntity<Usuario> registrar(@RequestBody Usuario usuario) {
        Usuario nuevo = usuarioService.guardar(usuario);
        return ResponseEntity.ok(nuevo);
    }

    @GetMapping("/mostar-boton/{codigoBoton}")
    public boolean mostrarBoton(@PathVariable String codigoBoton) {
        return usuarioService.mostrarBoton(codigoBoton);
    }

    @PostMapping("/recovery/account")
    public ResponseEntity<RecoveryLookupResponse> lookupRecoveryAccount(
            @RequestBody RecoveryLookupRequest request
    ) {
        try {
            return ResponseEntity.ok(authService.lookupRecoveryAccount(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(RecoveryLookupResponse.builder()
                            .accountFound(false)
                            .hasRecoveryData(false)
                            .message("No encontramos una cuenta con esos datos")
                            .build());
        }
    }

    @PostMapping("/recovery/verify-answer")
    public ResponseEntity<?> verifyRecoveryAnswer(
            @RequestBody RecoveryVerifyAnswerRequest request
    ) {
        try {
            boolean valid = authService.verifyRecoveryAnswer(request);
            if (!valid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(java.util.Map.of(
                                "success", false,
                                "message", "La respuesta secreta no coincide"
                        ));
            }
            return ResponseEntity.ok(java.util.Map.of(
                    "success", true,
                    "message", "Respuesta correcta"
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                    .body(java.util.Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(java.util.Map.of("success", false, "message", "Cuenta no encontrada"));
        }
    }

    @PostMapping("/recovery/reset-password")
    public ResponseEntity<?> resetPasswordByRecovery(
            @RequestBody RecoveryResetPasswordRequest request
    ) {
        try {
            authService.resetPasswordByRecovery(request);
            return ResponseEntity.ok(java.util.Map.of(
                    "success", true,
                    "message", "Clave actualizada correctamente"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of("success", false, "message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                    .body(java.util.Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(java.util.Map.of("success", false, "message", "Cuenta no encontrada"));
        }
    }
}
