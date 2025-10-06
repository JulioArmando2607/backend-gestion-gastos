package com.gestion.gastos.repository;

import com.gestion.gastos.model.dto.proyección.CardPersonalizadoResumen;
import com.gestion.gastos.model.dto.proyección.CategoriaPersonalizadoProjection;
import com.gestion.gastos.model.entity.CardPersonalizadoEntity;
import com.gestion.gastos.model.entity.CategoriaPersonalizadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardPersonalizadoRepository extends JpaRepository<CardPersonalizadoEntity, Long> {//

    @Query(value = """
      SELECT
        cp.id                              AS id,
        cp.nombre                          AS nombre,
        cp.color_hex                       AS colorHex,
        cp.descripcion                       AS descripcion,
        cp.moneda                       AS moneda,
        COALESCE(SUM(CASE WHEN m.tipo='INGRESO' THEN m.monto END), 0) AS ingresos,
        COALESCE(SUM(CASE WHEN m.tipo='GASTO'   THEN m.monto END), 0) AS gastos,
        COALESCE(SUM(CASE 
                     WHEN m.tipo='INGRESO' THEN m.monto
                     WHEN m.tipo='GASTO'   THEN -m.monto
                   END), 0) AS saldo
      FROM cards_personalizado cp
      LEFT JOIN movimientos_personalizado m
             ON m.card_id = cp.id and m.activo = 1
      WHERE cp.user_id = :userId
        AND cp.archivado = 0
      GROUP BY cp.id, cp.nombre, cp.color_hex
      ORDER BY cp.created_at DESC
      """, nativeQuery = true)
    List<CardPersonalizadoResumen> listarResumenPorUsuario(@Param("userId") Long userId);

    boolean existsByUserIdAndNombreIgnoreCase(Long userId, String nombre);

    @Query(value = """
      SELECT id,nombre from categorias_personalizado where activa = 1 and user_id = :userId and card_id = :idCard
      """, nativeQuery = true)
    List<CategoriaPersonalizadoProjection> listCategoriaPersonalizado(@Param("userId") Long userId, @Param("idCard") int idCard);


    @Query(value = """
      SELECT id,nombre from categorias_personalizado where activa = 1 and user_id = :userId and card_id = :idCard and tipo=:tipo
      """, nativeQuery = true)
    List<CategoriaPersonalizadoProjection> listCategoriaPersonalizadoxTipo(@Param("userId") Long userId,
                                                                           @Param("idCard") int idCard,
                                                                           @Param("tipo") String tipo
    );

    @Query(value = """
      SELECT
        cp.id                              AS id,
        cp.nombre                          AS nombre,
        cp.color_hex                       AS colorHex,
        cp.descripcion                       AS descripcion,
        cp.moneda                       AS moneda,
        COALESCE(SUM(CASE WHEN m.tipo='INGRESO' THEN m.monto END), 0) AS ingresos,
        COALESCE(SUM(CASE WHEN m.tipo='GASTO'   THEN m.monto END), 0) AS gastos,
        COALESCE(SUM(CASE 
                     WHEN m.tipo='INGRESO' THEN m.monto
                     WHEN m.tipo='GASTO'   THEN -m.monto
                   END), 0) AS saldo
      FROM cards_personalizado cp
      LEFT JOIN movimientos_personalizado m
             ON m.card_id = cp.id and m.activo = 1
      WHERE cp.user_id = :userId
        AND cp.archivado = 0 and cp.id = :idCard
      GROUP BY cp.id, cp.nombre, cp.color_hex
      ORDER BY cp.created_at DESC
      """, nativeQuery = true)
    CardPersonalizadoResumen CardPersonalizadosxId(@Param("userId") Integer userId,@Param("idCard") Integer idCard);
}
