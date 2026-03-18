package com.gestion.gastos.repository;

import com.gestion.gastos.model.dto.ReporteCategoriaProjection;
import com.gestion.gastos.model.dto.ReporteDescripcionProjection;
import com.gestion.gastos.model.dto.cardResumenResponse;
import com.gestion.gastos.model.dto.proyección.DashboardProjection;
import com.gestion.gastos.model.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByActivoTrue();

   // List<Movimiento> findByUsuarioIdAndActivoTrueOrderByIdDesc(Long usuarioId);
   // List<Movimiento> findByUsuarioIdAndActivoTrueOrderByFechaDescAndFechaRegDesc(Long usuarioId);
  //  List<Movimiento> findByUsuarioIdAndActivoTrueOrderByFechaDescFechaRegDesc(Long usuarioId);
    List<Movimiento> findByUsuarioIdAndActivoTrueOrderByFechaDescCreadoEnDesc(Long usuarioId);

    @Query(value = """
        SELECT
            SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.monto ELSE 0 END) AS totalIngresos,
            SUM(CASE WHEN m.tipo = 'GASTO' THEN m.monto ELSE 0 END) AS totalGastos,
            SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.monto ELSE 0 END) -
            SUM(CASE WHEN m.tipo = 'GASTO' THEN m.monto ELSE 0 END) AS saldoTotal
        FROM movimientos m
        WHERE m.usuario_id = :usuario AND m.activo = 1
        """, nativeQuery = true)
    cardResumenResponse cardResumen(@Param("usuario") Long usuario);

    @Query(value = """
        SELECT
            MONTH(mp.fecha) AS mesNum,
            DATE_FORMAT(mp.fecha, '%Y-%m') AS mesTexto,
            SUM(CASE WHEN mp.tipo = 'GASTO' THEN mp.monto ELSE 0 END) AS gastoTotal,
            SUM(CASE WHEN mp.tipo = 'INGRESO' THEN mp.monto ELSE 0 END) AS ingresoTotal
        FROM movimientos mp
        WHERE mp.activo = 1
          AND mp.usuario_id = :idUsuario
          AND YEAR(mp.fecha) = :anio
          AND MONTH(mp.fecha) = :mes
        GROUP BY MONTH(mp.fecha), DATE_FORMAT(mp.fecha, '%Y-%m')
        ORDER BY mesNum
        """, nativeQuery = true)
    List<DashboardProjection> listarDashboard(
            @Param("idUsuario") Long idUsuario,
            @Param("mes") Long mes,
            @Param("anio") Long anio
    );

    @Query(value = """
        SELECT
            CASE
                WHEN m.descripcion IS NULL OR TRIM(m.descripcion) = '' THEN 'Sin Descripcion'
                ELSE m.descripcion
            END AS descripcion,
            c.nombre AS nombre,
            CAST(SUM(m.monto) AS DECIMAL(18,2)) AS total
        FROM movimientos m
        INNER JOIN categorias c ON m.categoria_id = c.id
        WHERE m.tipo = :tipo
          AND m.activo = 1
          AND m.usuario_id = :usuarioId
          AND (
                (:fechaInicio IS NULL AND :fechaFin IS NULL)
                OR m.fecha BETWEEN :fechaInicio AND :fechaFin
              )
          AND (
                (:anio IS NULL AND :mes IS NULL)
                OR (YEAR(m.fecha) = :anio AND MONTH(m.fecha) = :mes)
              )
        GROUP BY
            CASE
                WHEN m.descripcion IS NULL OR TRIM(m.descripcion) = '' THEN 'Sin Descripcion'
                ELSE m.descripcion
            END,
            c.nombre
        ORDER BY total DESC
        """, nativeQuery = true)
    List<ReporteDescripcionProjection> reportePorDescripcion(
            @Param("usuarioId") Long usuarioId,
            @Param("tipo") String tipo,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("anio") Integer anio,
            @Param("mes") Integer mes
    );

    @Query(value = """
        SELECT
            c.id AS id,
            c.nombre AS nombre,
            CAST(SUM(m.monto) AS DECIMAL(18,2)) AS total
        FROM categorias c
        INNER JOIN movimientos m ON m.categoria_id = c.id
        WHERE m.tipo = :tipo
          AND m.activo = 1
          AND m.usuario_id = :usuarioId
          AND (
                (:fechaInicio IS NULL AND :fechaFin IS NULL)
                OR m.fecha BETWEEN :fechaInicio AND :fechaFin
              )
          AND (
                (:anio IS NULL AND :mes IS NULL)
                OR (YEAR(m.fecha) = :anio AND MONTH(m.fecha) = :mes)
              )
        GROUP BY c.id, c.nombre
        ORDER BY total DESC
        """, nativeQuery = true)
    List<ReporteCategoriaProjection> reportePorCategoria(
            @Param("usuarioId") Long usuarioId,
            @Param("tipo") String tipo,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("anio") Integer anio,
            @Param("mes") Integer mes
    );
}
