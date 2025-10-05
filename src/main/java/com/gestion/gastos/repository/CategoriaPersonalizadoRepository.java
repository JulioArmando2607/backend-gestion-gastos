package com.gestion.gastos.repository;

import com.gestion.gastos.model.entity.Categoria;
import com.gestion.gastos.model.entity.CategoriaPersonalizadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaPersonalizadoRepository extends JpaRepository<CategoriaPersonalizadoEntity, Long> {
    boolean existsByUserIdAndNombreIgnoreCase(Long id, String nombre);
}
