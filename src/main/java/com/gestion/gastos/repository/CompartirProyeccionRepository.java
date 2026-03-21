package com.gestion.gastos.repository;

import com.gestion.gastos.model.dto.CompartirEnviadaProjection;
import com.gestion.gastos.model.entity.CompartirProyeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompartirProyeccionRepository extends JpaRepository<CompartirProyeccion, Integer> {

    boolean existsByIdPersonaCompartioAndIdPersonaCompartidaAndIdProyeccionAndActivoTrue(
            Integer idPersonaCompartio,
            Integer idPersonaCompartida,
            Integer idProyeccion
    );

    boolean existsByIdPersonaCompartidaAndIdProyeccionAndActivoTrue(
            Integer idPersonaCompartida,
            Integer idProyeccion
    );

    @Query(value = """
            SELECT COUNT(*)
            FROM compartir_proyeccion cp
            WHERE cp.id_persona_compartida = :idPersonaCompartida
              AND cp.id_proyeccion = :idProyeccion
              AND cp.activo = 1
              AND (cp.permiso IS NULL OR UPPER(cp.permiso) = UPPER(:permiso))
            """, nativeQuery = true)
    long countAccessByPermiso(
            @Param("idPersonaCompartida") Integer idPersonaCompartida,
            @Param("idProyeccion") Integer idProyeccion,
            @Param("permiso") String permiso
    );

    boolean existsByIdAndIdPersonaCompartioAndActivoTrue(
            Integer id,
            Integer idPersonaCompartio
    );

    @Query(value = """
            SELECT
                cp.id AS idCompartir,
                cp.id_proyeccion AS idProyeccion,
                pm.usuario_id AS ownerUserId,
                cp.id_persona_compartida AS idPersonaCompartida,
                u.nombre AS nombrePersonaCompartida,
                u.email AS correoPersonaCompartida,
                COALESCE(cp.permiso, 'EDITAR') AS permiso,
                pm.anio AS anio,
                pm.mes AS mes,
                cp.fecha_reg AS fechaCompartido
            FROM compartir_proyeccion cp
            INNER JOIN proyeccion_mensual pm ON pm.id = cp.id_proyeccion
            INNER JOIN personas p ON p.id = cp.id_persona_compartio
            INNER JOIN usuarios u ON u.id = p.usuario_id
            
            INNER JOIN personas pido ON pido.id = cp.id_persona_compartida
            INNER JOIN usuarios uido ON uido.id = pido.usuario_id
            
            WHERE uido.id = :idPersonaCompartida
              AND cp.activo = 1
            ORDER BY cp.id DESC
            """, nativeQuery = true)
    List<CompartirEnviadaProjection> listarRecibidos(@Param("idPersonaCompartida") Integer idPersonaCompartida);

    List<CompartirProyeccion> findByIdPersonaCompartioAndActivoTrueOrderByIdDesc(Integer idPersonaCompartio);

    @Query(value = """
            SELECT
                cp.id AS idCompartir,
                cp.id_proyeccion AS idProyeccion,
                pm.usuario_id AS ownerUserId,
                cp.id_persona_compartida AS idPersonaCompartida,
                uido.nombre AS nombrePersonaCompartida,
                uido.email AS correoPersonaCompartida,
                COALESCE(cp.permiso, 'EDITAR') AS permiso,
                pm.anio AS anio,
                pm.mes AS mes,
                cp.fecha_reg AS fechaCompartido
            FROM compartir_proyeccion cp
            INNER JOIN proyeccion_mensual pm ON pm.id = cp.id_proyeccion
            INNER JOIN personas p ON p.id = cp.id_persona_compartio
            INNER JOIN usuarios u ON u.id = p.usuario_id
            
            INNER JOIN personas pido ON pido.id = cp.id_persona_compartida
            INNER JOIN usuarios uido ON uido.id = pido.usuario_id
            
            WHERE u.id = :idPersona and cp.id_proyeccion = :idProyeccion
              AND cp.activo = 1
            ORDER BY cp.id DESC
            """, nativeQuery = true)
    List<CompartirEnviadaProjection> listarEnviadasDetalle(@Param("idPersona") Integer idPersona,
                                                           @Param("idProyeccion") Integer idProyeccion);
}
