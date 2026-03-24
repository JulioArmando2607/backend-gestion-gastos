package com.gestion.gastos.repository;

import com.gestion.gastos.model.dto.CompartirGastoPersonalizadoProjection;
import com.gestion.gastos.model.entity.CompartirGastoPersonalizado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompartirGastoPersonalizadoRepository extends JpaRepository<CompartirGastoPersonalizado, Integer> {

    boolean existsByIdPersonaCompartioAndIdPersonaCompartidaAndGastoPersonalizadoIdAndActivoTrue(
            Integer idPersonaCompartio,
            Integer idPersonaCompartida,
            Long gastoPersonalizadoId
    );

    boolean existsByIdAndIdPersonaCompartioAndActivoTrue(
            Integer id,
            Integer idPersonaCompartio
    );

    boolean existsByIdPersonaCompartidaAndGastoPersonalizadoIdAndActivoTrue(
            Integer idPersonaCompartida,
            Long gastoPersonalizadoId
    );

    @Query(value = """
            SELECT COUNT(*)
            FROM compartir_gasto_personalizado cgp
            WHERE cgp.id_persona_compartida = :idPersonaCompartida
              AND cgp.gasto_personalizado = :gastoPersonalizadoId
              AND cgp.activo = 1
              AND (cgp.permiso IS NULL OR UPPER(cgp.permiso) = UPPER(:permiso))
            """, nativeQuery = true)
    long countAccessByPermiso(
            @Param("idPersonaCompartida") Integer idPersonaCompartida,
            @Param("gastoPersonalizadoId") Long gastoPersonalizadoId,
            @Param("permiso") String permiso
    );

    @Query(value = """
            SELECT DISTINCT u.id
            FROM compartir_gasto_personalizado cgp
            INNER JOIN personas p ON p.id = cgp.id_persona_compartida
            INNER JOIN usuarios u ON u.id = p.usuario_id
            WHERE cgp.gasto_personalizado = :idGastoPersonalizado
              AND cgp.activo = 1
            """, nativeQuery = true)
    List<Long> findRecipientUserIdsByGastoPersonalizadoId(@Param("idGastoPersonalizado") Long idGastoPersonalizado);

    @Query(value = """
            SELECT
                cgp.id AS idCompartir,
                cgp.gasto_personalizado AS idGastoPersonalizado,
                cp.user_id AS ownerUserId,
                cgp.id_persona_compartida AS idPersonaCompartida,
                cp.nombre AS nombreGastoPersonalizado,
                cp.descripcion AS descripcion,
                cp.moneda AS moneda,
                cp.color_hex AS colorHex,
                COALESCE(SUM(CASE WHEN mp.tipo='INGRESO' THEN mp.monto END), 0) AS ingresos,
                COALESCE(SUM(CASE WHEN mp.tipo='GASTO' THEN mp.monto END), 0) AS gastos,
                CASE
                    WHEN UPPER(COALESCE(cp.descripcion, '')) = 'TARJ. CRED' THEN
                        COALESCE(cp.monto, 0) + COALESCE(SUM(CASE
                            WHEN mp.tipo='INGRESO' THEN mp.monto
                            WHEN mp.tipo='GASTO' THEN -mp.monto
                        END), 0)
                    ELSE COALESCE(SUM(CASE
                            WHEN mp.tipo='INGRESO' THEN mp.monto
                            WHEN mp.tipo='GASTO' THEN -mp.monto
                        END), 0)
                END AS saldo,
                u.nombre AS nombreRelacionada,
                u.email AS correoRelacionada,
                COALESCE(cgp.permiso, 'EDITAR') AS permiso,
                cgp.fecha_reg AS fechaCompartido
            FROM compartir_gasto_personalizado cgp
            INNER JOIN cards_personalizado cp ON cp.id = cgp.gasto_personalizado
            LEFT JOIN movimientos_personalizado mp ON mp.card_id = cp.id AND mp.activo = 1
            INNER JOIN personas p ON p.id = cgp.id_persona_compartio
            INNER JOIN usuarios u ON u.id = p.usuario_id
            INNER JOIN personas pd ON pd.id = cgp.id_persona_compartida
            INNER JOIN usuarios ud ON ud.id = pd.usuario_id
            WHERE ud.id = :idUsuario
              AND cgp.activo = 1
            GROUP BY cgp.id, cgp.gasto_personalizado, cp.user_id, cgp.id_persona_compartida,
                cp.nombre, cp.descripcion, cp.moneda, cp.color_hex, cp.monto, u.nombre, u.email, cgp.permiso, cgp.fecha_reg
            ORDER BY cgp.id DESC
            """, nativeQuery = true)
    List<CompartirGastoPersonalizadoProjection> listarRecibidos(@Param("idUsuario") Integer idUsuario);

    @Query(value = """
            SELECT
                cgp.id AS idCompartir,
                cgp.gasto_personalizado AS idGastoPersonalizado,
                cp.user_id AS ownerUserId,
                cgp.id_persona_compartida AS idPersonaCompartida,
                cp.nombre AS nombreGastoPersonalizado,
                cp.descripcion AS descripcion,
                cp.moneda AS moneda,
                cp.color_hex AS colorHex,
                COALESCE(SUM(CASE WHEN mp.tipo='INGRESO' THEN mp.monto END), 0) AS ingresos,
                COALESCE(SUM(CASE WHEN mp.tipo='GASTO' THEN mp.monto END), 0) AS gastos,
                CASE
                    WHEN UPPER(COALESCE(cp.descripcion, '')) = 'TARJ. CRED' THEN
                        COALESCE(cp.monto, 0) + COALESCE(SUM(CASE
                            WHEN mp.tipo='INGRESO' THEN mp.monto
                            WHEN mp.tipo='GASTO' THEN -mp.monto
                        END), 0)
                    ELSE COALESCE(SUM(CASE
                            WHEN mp.tipo='INGRESO' THEN mp.monto
                            WHEN mp.tipo='GASTO' THEN -mp.monto
                        END), 0)
                END AS saldo,
                ud.nombre AS nombreRelacionada,
                ud.email AS correoRelacionada,
                COALESCE(cgp.permiso, 'EDITAR') AS permiso,
                cgp.fecha_reg AS fechaCompartido
            FROM compartir_gasto_personalizado cgp
            INNER JOIN cards_personalizado cp ON cp.id = cgp.gasto_personalizado
            LEFT JOIN movimientos_personalizado mp ON mp.card_id = cp.id AND mp.activo = 1
            INNER JOIN personas p ON p.id = cgp.id_persona_compartio
            INNER JOIN usuarios u ON u.id = p.usuario_id
            INNER JOIN personas pd ON pd.id = cgp.id_persona_compartida
            INNER JOIN usuarios ud ON ud.id = pd.usuario_id
            WHERE u.id = :idUsuario
              AND cgp.activo = 1
            GROUP BY cgp.id, cgp.gasto_personalizado, cp.user_id, cgp.id_persona_compartida,
                cp.nombre, cp.descripcion, cp.moneda, cp.color_hex, cp.monto, ud.nombre, ud.email, cgp.permiso, cgp.fecha_reg
            ORDER BY cgp.id DESC
            """, nativeQuery = true)
    List<CompartirGastoPersonalizadoProjection> listarEnviados(@Param("idUsuario") Integer idUsuario);
}

