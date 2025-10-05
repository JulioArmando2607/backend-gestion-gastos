package com.gestion.gastos.controller;

import com.gestion.gastos.model.entity.Categoria;
import com.gestion.gastos.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<Categoria> registrar(@RequestBody Categoria categoria) {
        return ResponseEntity.ok(categoriaService.guardar(categoria));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Categoria>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(categoriaService.listarPorUsuario(usuarioId));
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> listarTodo() {
        return ResponseEntity.ok(categoriaService.listarTodo());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Categoria>> listaCategoriaxTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(categoriaService.listaCategoriaxTipo(tipo));
    }

}
