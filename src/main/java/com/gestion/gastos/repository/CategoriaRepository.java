package com.gestion.gastos.repository;

import com.gestion.gastos.model.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByUsuarioIdOrderByNombreAsc(Long usuarioId);

    @Query(value = """
        SELECT *
        FROM categorias
        WHERE tipo = :tipo
        ORDER BY nombre ASC
        """, nativeQuery = true)
    List<Categoria> findByTipo(@Param("tipo") String tipo);

    @Query(value = """
        SELECT *
        FROM categorias
        WHERE tipo = :tipo
          AND (
            COALESCE(es_predeterminada, '0') = '1'
            OR (usuario_id = :usuarioId AND COALESCE(es_predeterminada, '0') = '0')
          )
        ORDER BY CASE WHEN COALESCE(es_predeterminada, '0') = '1' THEN 0 ELSE 1 END,
                 nombre ASC
        """, nativeQuery = true)
    List<Categoria> findByTipoAndUsuarioUnificado(
            @Param("tipo") String tipo,
            @Param("usuarioId") Long usuarioId
    );
}
