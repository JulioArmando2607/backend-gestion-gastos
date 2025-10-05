package com.gestion.gastos.repository;

import com.gestion.gastos.model.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByUsuarioId(Long usuarioId);

    @Query(value = """
        SELECT * from categorias WHERE tipo = :tipo
        """,
            nativeQuery = true)
    List<Categoria> findByTipo(@Param("tipo")String tipo);
}
