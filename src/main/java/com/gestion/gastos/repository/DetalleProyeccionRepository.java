package com.gestion.gastos.repository;

import com.gestion.gastos.model.entity.DetalleProyeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetalleProyeccionRepository extends JpaRepository<DetalleProyeccion, Integer> {

    Optional<DetalleProyeccion> findByProyeccionIdAndCategoriaId(Integer proyeccionId, Integer categoriaId);

    List<DetalleProyeccion> findByProyeccionId(Integer proyeccionId);

    @Query("SELECT d FROM DetalleProyeccion d " +
            "WHERE d.proyeccion.id = :proyeccionId")
    List<DetalleProyeccion> findAllByProyeccionId(@Param("proyeccionId") Integer proyeccionId);
}