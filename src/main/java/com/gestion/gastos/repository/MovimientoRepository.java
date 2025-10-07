package com.gestion.gastos.repository;

import com.gestion.gastos.model.dto.cardResumenResponse;
import com.gestion.gastos.model.dto.proyección.DashboardProjection;
import com.gestion.gastos.model.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    // Listar todos los movimientos de un usuario
    List<Movimiento> findByActivoTrue();

    List<Movimiento> findByUsuarioIdAndActivoTrueOrderByIdDesc(Long usuarioId);

    @Query(value = """
        SELECT
            SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.monto ELSE 0 END) AS totalIngresos,
            SUM(CASE WHEN m.tipo = 'GASTO' THEN m.monto ELSE 0 END) AS totalGastos,
            SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.monto ELSE 0 END) -
            SUM(CASE WHEN m.tipo = 'GASTO' THEN m.monto ELSE 0 END) AS saldoTotal
        FROM movimientos m
        WHERE m.usuario_id = :usuario AND m.activo = 1;
        """,
            nativeQuery = true)
    cardResumenResponse cardResumen(@Param("usuario") Long usuario );


    @Query(value = """ 
    SELECT 
        MONTH(mp.fecha) AS mesNum, 
        DATE_FORMAT(mp.fecha, '%Y-%m') AS mesTexto, 
        SUM(mp.monto) AS gastoTotal 
    FROM 
        movimientos mp 
    WHERE 
        mp.activo = 1 
        AND mp.tipo = 'GASTO' 
        AND mp.usuario_id = :idUsuario 
        AND YEAR(mp.fecha) = :anio 
        AND MONTH(mp.fecha) = :mes
    GROUP BY 
        MONTH(mp.fecha), DATE_FORMAT(mp.fecha, '%Y-%m') 
    ORDER BY 
        mesNum
    """,
            nativeQuery = true)
    List<DashboardProjection> listarDashboard(@Param("idUsuario") Long idUsuario,
                                              @Param("mes") Long mes,
                                              @Param("anio") Long anio
    );
}
