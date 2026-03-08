package com.gestion.gastos.repository;

import com.gestion.gastos.model.dto.DetalleProyeccionView;
import com.gestion.gastos.model.entity.DetalleProyeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetalleProyeccionRepository extends JpaRepository<DetalleProyeccion, Integer> {

    Optional<DetalleProyeccion> findByProyeccionIdAndCategoriaIdAndMesAndAnio(Integer proyeccionId, Integer categoriaId, Integer mes, Integer anio);

    List<DetalleProyeccion> findByProyeccionId(Integer proyeccionId);

    @Query("SELECT d FROM DetalleProyeccion d " +
            "WHERE d.proyeccion.id = :proyeccionId")
    List<DetalleProyeccion> findAllByProyeccionId(@Param("proyeccionId") Integer proyeccionId);

    @Query("""
            SELECT d
            FROM DetalleProyeccion d
            WHERE d.proyeccion.id = :proyeccionId
              AND d.categoria.id = :categoriaId
            """)
    Optional<DetalleProyeccion> findByProyeccionIdAndCategoriaId(
            @Param("proyeccionId") Integer proyeccionId,
            @Param("categoriaId") Integer categoriaId
    );

    @Query("""
            SELECT
                d.id as idDetalle,
                d.proyeccion.id as idProyeccion,
                d.categoria.id as idCategoria,
                d.categoria.nombre as nombreCategoria,
                d.categoria.color as colorCategoria,
                d.montoProyectado as montoProyectado,
                d.montoReal as montoReal,
                d.notas as notas,
                d.anio as anio,
                d.mes as mes,
                d.fechaCreacion as fechaCreacion,
                d.fechaActualizacion as fechaActualizacion
            FROM DetalleProyeccion d
            WHERE d.proyeccion.id = :proyeccionId
            ORDER BY d.id DESC
            """)
    List<DetalleProyeccionView> findDetalleViewByProyeccionId(@Param("proyeccionId") Integer proyeccionId);
}
