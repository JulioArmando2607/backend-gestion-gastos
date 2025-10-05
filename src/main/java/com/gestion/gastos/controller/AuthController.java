package com.gestion.gastos.controller;

import com.gestion.gastos.model.dto.LoginRequest;
import com.gestion.gastos.model.dto.LoginResponse;
import com.gestion.gastos.model.entity.Usuario;
import com.gestion.gastos.service.AuthService;
import com.gestion.gastos.service.UsuarioService;
import lombok.RequiredArgsConstructor;
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
}
