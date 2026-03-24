package com.gestion.gastos.repository;

import com.gestion.gastos.model.dto.proyección.CardPersonalizadoResumen;
import com.gestion.gastos.model.dto.proyección.CategoriaPersonalizadoProjection;
import com.gestion.gastos.model.entity.CardPersonalizadoEntity;
import com.gestion.gastos.model.entity.CategoriaPersonalizadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CardPersonalizadoRepository extends JpaRepository<CardPersonalizadoEntity, Long> {//

    @Query(value = """
      SELECT
        cp.id                              AS id,
        cp.nombre                          AS nombre,
        cp.color_hex                       AS colorHex,
        cp.descripcion                       AS descripcion,
        cp.moneda                       AS moneda,
        cp.monto                        AS monto,
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
      GROUP BY cp.id, cp.nombre, cp.color_hex, cp.descripcion, cp.moneda, cp.monto
      ORDER BY cp.id DESC
      """, nativeQuery = true)
    List<CardPersonalizadoResumen> listarResumenPorUsuario(@Param("userId") Long userId);

    boolean existsByUserIdAndNombreIgnoreCase(Long userId, String nombre);
    boolean existsByUserIdAndNombreIgnoreCaseAndArchivadoFalse(Long userId, String nombre);
    boolean existsByUserIdAndNombreIgnoreCaseAndIdNot(Long userId, String nombre, Long id);
    Optional<CardPersonalizadoEntity> findByIdAndUserId(Long id, Long userId);
    Optional<CardPersonalizadoEntity> findById(Long id);

    @Query(value = """
      SELECT id,nombre from categorias_personalizado where activa = 1 and card_id = :idCard
      """, nativeQuery = true)
    List<CategoriaPersonalizadoProjection> listCategoriaPersonalizado(@Param("idCard") int idCard);


    @Query(value = """
      SELECT id,nombre from categorias_personalizado where activa = 1 and card_id = :idCard and tipo=:tipo
      """, nativeQuery = true)
    List<CategoriaPersonalizadoProjection> listCategoriaPersonalizadoxTipo(@Param("idCard") int idCard,
                                                                           @Param("tipo") String tipo
    );

    @Query(value = """
      SELECT
        cp.id                              AS id,
        cp.nombre                          AS nombre,
        cp.color_hex                       AS colorHex,
        cp.descripcion                       AS descripcion,
        cp.moneda                       AS moneda,
        cp.monto                        AS monto,
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
      GROUP BY cp.id, cp.nombre, cp.color_hex, cp.descripcion, cp.moneda, cp.monto
      ORDER BY cp.created_at DESC
      """, nativeQuery = true)
    CardPersonalizadoResumen CardPersonalizadosxId(@Param("userId") Integer userId,@Param("idCard") Integer idCard);

    @Query(value = """
      SELECT
        cp.id                              AS id,
        cp.nombre                          AS nombre,
        cp.color_hex                       AS colorHex,
        cp.descripcion                     AS descripcion,
        cp.moneda                          AS moneda,
        cp.monto                           AS monto,
        COALESCE(SUM(CASE WHEN m.tipo='INGRESO' THEN m.monto END), 0) AS ingresos,
        COALESCE(SUM(CASE WHEN m.tipo='GASTO'   THEN m.monto END), 0) AS gastos,
        COALESCE(SUM(CASE 
                     WHEN m.tipo='INGRESO' THEN m.monto
                     WHEN m.tipo='GASTO'   THEN -m.monto
                   END), 0) AS saldo
      FROM cards_personalizado cp
      LEFT JOIN movimientos_personalizado m
             ON m.card_id = cp.id and m.activo = 1
      WHERE cp.archivado = 0
        AND cp.id = :idCard
      GROUP BY cp.id, cp.nombre, cp.color_hex, cp.descripcion, cp.moneda, cp.monto
      """, nativeQuery = true)
    CardPersonalizadoResumen CardPersonalizadosxIdSinValidacion(@Param("idCard") Long idCard);
}

