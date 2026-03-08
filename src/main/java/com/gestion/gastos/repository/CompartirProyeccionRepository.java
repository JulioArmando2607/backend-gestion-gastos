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
            SELECT
                cp.id AS idCompartir,
                cp.id_proyeccion AS idProyeccion,
                cp.id_persona_compartida AS idPersonaCompartida,
                u.nombre AS nombrePersonaCompartida,
                u.email AS correoPersonaCompartida,
                cp.fecha_reg AS fechaCompartido
            FROM compartir_proyeccion cp
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
                cp.id_persona_compartida AS idPersonaCompartida,
                uido.nombre AS nombrePersonaCompartida,
                uido.email AS correoPersonaCompartida,
                cp.fecha_reg AS fechaCompartido
            FROM compartir_proyeccion cp
            INNER JOIN personas p ON p.id = cp.id_persona_compartio
            INNER JOIN usuarios u ON u.id = p.usuario_id
            
            INNER JOIN personas pido ON pido.id = cp.id_persona_compartida
            INNER JOIN usuarios uido ON uido.id = pido.usuario_id
            
            WHERE u.id = :idPersona
              AND cp.activo = 1
            ORDER BY cp.id DESC
            """, nativeQuery = true)
    List<CompartirEnviadaProjection> listarEnviadasDetalle(@Param("idPersona") Integer idPersona);
}
