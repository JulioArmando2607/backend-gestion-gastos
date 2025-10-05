package com.gestion.gastos.service;

import com.gestion.gastos.model.entity.Categoria;
import com.gestion.gastos.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public List<Categoria> listarPorUsuario(Long usuarioId) {
        return categoriaRepository.findByUsuarioId(usuarioId);
    }

    public List<Categoria> listarTodo() {
        return categoriaRepository.findAll();
    }

    public void eliminar(Long id) {
        categoriaRepository.deleteById(id);
    }

    public List<Categoria> listaCategoriaxTipo(String tipo) {
        return categoriaRepository.findByTipo(tipo);
    }
}
