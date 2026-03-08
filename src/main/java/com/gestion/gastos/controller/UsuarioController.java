package com.gestion.gastos.controller;

import com.gestion.gastos.model.dto.PersonaUsuarioDto;
import com.gestion.gastos.model.entity.Personas;
import com.gestion.gastos.model.entity.Usuario;
import com.gestion.gastos.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuario")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // útil para Flutter web
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/persona")
    public ResponseEntity<PersonaUsuarioDto> obtenerPersona(@PathVariable Long id) {
        return usuarioService.buscarPersonaConUsuario(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/editar/{id}")
    public ResponseEntity<Usuario> editar(@PathVariable Long id,@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.editar(id, usuario));
    }

    @PostMapping("/actulizar-persona")
    public ResponseEntity<PersonaUsuarioDto> actualizarPersona(@RequestBody PersonaUsuarioDto personas) {
        return ResponseEntity.ok(usuarioService.actualizarPersona(personas));
    }
}
