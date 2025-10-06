package com.gestion.gastos.repository;

import com.gestion.gastos.model.dto.MovimientoPersonalizado;
import com.gestion.gastos.model.dto.cardResumenResponse;
import com.gestion.gastos.model.dto.proyección.MovimientoPersonalizadoView;
import com.gestion.gastos.model.dto.proyección.ReporteMovimientoPersonalizadoView;
import com.gestion.gastos.model.entity.MovimientoPersonalizadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovimientoPersonalizadoRepository extends JpaRepository<MovimientoPersonalizadoEntity, Long> {

    @Query(value = """
            SELECT
              mp.id,
              mp.tipo,
              mp.monto,
              mp.nota,
              mp.fecha,
             cp.nombre as categoria
            FROM
              movimientos_personalizado mp
              inner join categorias_personalizado cp on cp.id = mp.categoria_id
            
            WHERE
              mp.activo = 1
              AND mp.card_id = :cardId 
              
            order by  mp.id desc
              
            """,
            nativeQuery = true)
    List<MovimientoPersonalizadoView> listMovimientoPersonalizado(@Param("cardId") Long cardId );


        @Query(value = """
              SELECT\s
                    DATE_FORMAT(mp.fecha, '%Y-%m') AS mes,
                    mp.fecha,
                    mp.tipo,
                    CONCAT(mp.nota, '-',cp.nombre) as item,
                    SUM(mp.monto) AS monto,
                    COUNT(*) AS cantidad_movimientos
                  FROM movimientos_personalizado mp
                  inner join categorias_personalizado cp on mp.categoria_id =cp.id
                  WHERE mp.activo = 1 and mp.card_id = :cardId 
                  GROUP BY DATE_FORMAT(mp.fecha, '%Y-%m'), tipo,nota
                  ORDER BY mp.id desc;
            """,
            nativeQuery = true)
    List<ReporteMovimientoPersonalizadoView> listarReporteCard(@Param("cardId") Long cardId );

    @Query(value = """
            SELECT  
              mp.id,
              mp.tipo,
              mp.monto,
              mp.nota,
              mp.fecha,
             cp.nombre as categoria,
             cp.id as idCategoria
            FROM
              movimientos_personalizado mp
              inner join categorias_personalizado cp on cp.id = mp.categoria_id
            
            WHERE
              mp.activo = 1
              AND mp.id = :idMovimiento
              order by  mp.id desc
              
            """,
            nativeQuery = true)
    MovimientoPersonalizadoView obtenerMovimientoPersonalizado(@Param("idMovimiento") Long idMovimiento);
}
