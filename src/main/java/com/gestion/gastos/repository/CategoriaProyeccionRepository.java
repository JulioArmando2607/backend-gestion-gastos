package com.gestion.gastos.repository;

import com.gestion.gastos.model.dto.proyección.CategoriasProyeccionProjection;
import com.gestion.gastos.model.entity.CategoriaProyeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaProyeccionRepository extends JpaRepository<CategoriaProyeccion, Integer> {
    List<CategoriaProyeccion> findByUsuarioIdAndActivaTrueOrderByOrden(Integer usuarioId);
    boolean existsByUsuarioId(Integer usuarioId);

    // ⭐ QUERY PERSONALIZADA CON LEFT JOIN
    @Query(value = """
        SELECT 
            COALESCE(pm.id, 0) as proyeccionId,
            c.id as categoriaId,
            c.nombre as nombreCategoria,
            c.color as colorCategoria,
            COALESCE(d.monto_proyectado, 0) as montoCategoria,
            c.orden as ordenCategoria,
            COALESCE(pm.anio, :anio) as anio,
            COALESCE(pm.mes, :mes) as mes,
            COALESCE(pm.ingreso_mensual, 0) as ingresoMensual,
            COALESCE(pm.total_gastos, 0) as totalGasto,
            COALESCE(pm.ahorro_estimado, 0) as ahorroEstimado,
            COALESCE(pm.estado, 'NUEVA') as estado
        FROM categorias_proyeccion c
        LEFT JOIN detalle_proyeccion d ON c.id = d.id_categoria
        LEFT JOIN proyeccion_mensual pm ON d.id_proyeccion = pm.id 
            AND pm.usuario_id = :usuarioId
            AND pm.anio = :anio
            AND pm.mes = :mes
        WHERE c.usuario_id = :usuarioId
          AND c.activa = TRUE
        ORDER BY c.orden
        """, nativeQuery = true)
    List<CategoriasProyeccionProjection> findCategoriasConProyeccion(
            @Param("usuarioId") Integer usuarioId,
            @Param("anio") Integer anio,
            @Param("mes") Integer mes
    );
}
