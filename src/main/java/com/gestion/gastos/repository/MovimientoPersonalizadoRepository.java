package com.gestion.gastos.repository;

import com.gestion.gastos.model.dto.MovimientoPersonalizado;
import com.gestion.gastos.model.dto.cardResumenResponse;
import com.gestion.gastos.model.dto.proyección.MovimientoPersonalizadoView;
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
              AND mp.card_id = :cardId;
            """,
            nativeQuery = true)
    List<MovimientoPersonalizadoView> listMovimientoPersonalizado(@Param("cardId") Long cardId );

}
