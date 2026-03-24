package com.gestion.gastos.repository;

import com.gestion.gastos.model.dto.CompartirEnviadaProjection;
import com.gestion.gastos.model.dto.CompartirGastoPersonalizadoProjection;
import com.gestion.gastos.model.entity.CompartidoModulo;
import com.gestion.gastos.model.entity.ModuloCompartido;
import com.gestion.gastos.model.entity.PermisoCompartido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompartidoModuloRepository extends JpaRepository<CompartidoModulo, Long> {

    boolean existsByIdPersonaCompartioAndIdPersonaCompartidaAndModuloAndRecursoIdAndActivoTrue(
            Integer idPersonaCompartio,
            Integer idPersonaCompartida,
            ModuloCompartido modulo,
            Long recursoId
    );

    boolean existsByIdPersonaCompartidaAndModuloAndRecursoIdAndActivoTrue(
            Integer idPersonaCompartida,
            ModuloCompartido modulo,
            Long recursoId
    );

    boolean existsByIdAndIdPersonaCompartioAndActivoTrue(
            Long id,
            Integer idPersonaCompartio
    );

    Optional<CompartidoModulo> findByIdAndActivoTrue(Long id);

    List<CompartidoModulo> findByIdPersonaCompartioAndModuloAndActivoTrueOrderByIdDesc(
            Integer idPersonaCompartio,
            ModuloCompartido modulo
    );

    List<CompartidoModulo> findByIdPersonaCompartidaAndModuloAndActivoTrueOrderByIdDesc(
            Integer idPersonaCompartida,
            ModuloCompartido modulo
    );

    @Query("""
            select count(c) > 0
            from CompartidoModulo c
            where c.idPersonaCompartida = :idPersonaCompartida
              and c.modulo = :modulo
              and c.recursoId = :recursoId
              and c.activo = true
              and c.permiso = :permiso
            """)
    boolean hasPermiso(
            @Param("idPersonaCompartida") Integer idPersonaCompartida,
            @Param("modulo") ModuloCompartido modulo,
            @Param("recursoId") Long recursoId,
            @Param("permiso") PermisoCompartido permiso
    );

    @Query("""
            select c
            from CompartidoModulo c
            where c.modulo = :modulo
              and c.recursoId = :recursoId
              and c.activo = true
            order by c.id desc
            """)
    List<CompartidoModulo> findActivosByModuloAndRecursoId(
            @Param("modulo") ModuloCompartido modulo,
            @Param("recursoId") Long recursoId
    );

    @Query(value = """
            SELECT DISTINCT u.id
            FROM compartido_modulo cm
            INNER JOIN personas p ON p.id = cm.id_persona_compartida
            INNER JOIN usuarios u ON u.id = p.usuario_id
            WHERE cm.modulo = :modulo
              AND cm.recurso_id = :recursoId
              AND cm.activo = 1
            """, nativeQuery = true)
    List<Long> findRecipientUserIdsByModuloAndRecursoId(
            @Param("modulo") String modulo,
            @Param("recursoId") Long recursoId
    );

    @Query(value = """
            SELECT
                cm.id AS idCompartir,
                cm.recurso_id AS idGastoPersonalizado,
                cp.user_id AS ownerUserId,
                cm.id_persona_compartida AS idPersonaCompartida,
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
                COALESCE(cm.permiso, 'EDITAR') AS permiso,
                cm.fecha_reg AS fechaCompartido
            FROM compartido_modulo cm
            INNER JOIN cards_personalizado cp ON cp.id = cm.recurso_id
            LEFT JOIN movimientos_personalizado mp ON mp.card_id = cp.id AND mp.activo = 1
            INNER JOIN personas p ON p.id = cm.id_persona_compartio
            INNER JOIN usuarios u ON u.id = p.usuario_id
            INNER JOIN personas pd ON pd.id = cm.id_persona_compartida
            INNER JOIN usuarios ud ON ud.id = pd.usuario_id
            WHERE ud.id = :idUsuario
              AND cm.modulo = :modulo
              AND cm.activo = 1
            GROUP BY cm.id, cm.recurso_id, cp.user_id, cm.id_persona_compartida,
                cp.nombre, cp.descripcion, cp.moneda, cp.color_hex, cp.monto, u.nombre, u.email, cm.permiso, cm.fecha_reg
            ORDER BY cm.id DESC
            """, nativeQuery = true)
    List<CompartirGastoPersonalizadoProjection> listarGastosPersonalizadosRecibidos(
            @Param("idUsuario") Integer idUsuario,
            @Param("modulo") String modulo
    );

    @Query(value = """
            SELECT
                cm.id AS idCompartir,
                cm.recurso_id AS idGastoPersonalizado,
                cp.user_id AS ownerUserId,
                cm.id_persona_compartida AS idPersonaCompartida,
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
                COALESCE(cm.permiso, 'EDITAR') AS permiso,
                cm.fecha_reg AS fechaCompartido
            FROM compartido_modulo cm
            INNER JOIN cards_personalizado cp ON cp.id = cm.recurso_id
            LEFT JOIN movimientos_personalizado mp ON mp.card_id = cp.id AND mp.activo = 1
            INNER JOIN personas p ON p.id = cm.id_persona_compartio
            INNER JOIN usuarios u ON u.id = p.usuario_id
            INNER JOIN personas pd ON pd.id = cm.id_persona_compartida
            INNER JOIN usuarios ud ON ud.id = pd.usuario_id
            WHERE u.id = :idUsuario
              AND cm.modulo = :modulo
              AND cm.activo = 1
            GROUP BY cm.id, cm.recurso_id, cp.user_id, cm.id_persona_compartida,
                cp.nombre, cp.descripcion, cp.moneda, cp.color_hex, cp.monto, ud.nombre, ud.email, cm.permiso, cm.fecha_reg
            ORDER BY cm.id DESC
            """, nativeQuery = true)
    List<CompartirGastoPersonalizadoProjection> listarGastosPersonalizadosEnviados(
            @Param("idUsuario") Integer idUsuario,
            @Param("modulo") String modulo
    );

    @Query(value = """
            SELECT
                cm.id AS idCompartir,
                cm.recurso_id AS idProyeccion,
                pm.usuario_id AS ownerUserId,
                cm.id_persona_compartida AS idPersonaCompartida,
                u.nombre AS nombrePersonaCompartida,
                u.email AS correoPersonaCompartida,
                COALESCE(cm.permiso, 'EDITAR') AS permiso,
                pm.anio AS anio,
                pm.mes AS mes,
                cm.fecha_reg AS fechaCompartido
            FROM compartido_modulo cm
            INNER JOIN proyeccion_mensual pm ON pm.id = cm.recurso_id
            INNER JOIN personas p ON p.id = cm.id_persona_compartio
            INNER JOIN usuarios u ON u.id = p.usuario_id
            INNER JOIN personas pido ON pido.id = cm.id_persona_compartida
            INNER JOIN usuarios uido ON uido.id = pido.usuario_id
            WHERE uido.id = :idUsuario
              AND cm.modulo = :modulo
              AND cm.activo = 1
            ORDER BY cm.id DESC
            """, nativeQuery = true)
    List<CompartirEnviadaProjection> listarProyeccionesRecibidas(
            @Param("idUsuario") Integer idUsuario,
            @Param("modulo") String modulo
    );

    @Query(value = """
            SELECT
                cm.id AS idCompartir,
                cm.recurso_id AS idProyeccion,
                pm.usuario_id AS ownerUserId,
                cm.id_persona_compartida AS idPersonaCompartida,
                uido.nombre AS nombrePersonaCompartida,
                uido.email AS correoPersonaCompartida,
                COALESCE(cm.permiso, 'EDITAR') AS permiso,
                pm.anio AS anio,
                pm.mes AS mes,
                cm.fecha_reg AS fechaCompartido
            FROM compartido_modulo cm
            INNER JOIN proyeccion_mensual pm ON pm.id = cm.recurso_id
            INNER JOIN personas p ON p.id = cm.id_persona_compartio
            INNER JOIN usuarios u ON u.id = p.usuario_id
            INNER JOIN personas pido ON pido.id = cm.id_persona_compartida
            INNER JOIN usuarios uido ON uido.id = pido.usuario_id
            WHERE u.id = :idUsuario
              AND cm.recurso_id = :recursoId
              AND cm.modulo = :modulo
              AND cm.activo = 1
            ORDER BY cm.id DESC
            """, nativeQuery = true)
    List<CompartirEnviadaProjection> listarProyeccionesEnviadas(
            @Param("idUsuario") Integer idUsuario,
            @Param("recursoId") Long recursoId,
            @Param("modulo") String modulo
    );
}

